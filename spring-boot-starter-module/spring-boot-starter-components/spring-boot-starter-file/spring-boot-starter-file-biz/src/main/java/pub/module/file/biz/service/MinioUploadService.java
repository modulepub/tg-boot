package pub.module.file.biz.service;

import cn.hutool.core.io.FileUtil;
import lombok.Cleanup;
import pub.module.file.BizFileAutoConfiguration;
import com.google.common.collect.Sets;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实现Minio文件分片上传
 * @author panzhen
 */
@Slf4j
@Service
public class MinioUploadService {
    @Resource
    BizFileAutoConfiguration config;
    @Resource
    MinioClient minioClient;

    /**
     *
     * @param file 上传文件
     * @param md5 上传文件的唯一标识，建议是md5
     * @param sliceIndex 上传文件的分片序号
     * @param totalPieces 上传文件的分片总数
     * @param filePath  上传文件的文件名
     */
    @SneakyThrows
    public int uploadByFragment(File file, String md5, Integer sliceIndex, Integer totalPieces,String filePath) {
        String bucketName = config.getMinioBucketName();
        log.info("UploadByFragment --- 上传文件的md5: {}", md5);
        // 调用分片上传的逻辑
        int index = uploadFileByFragment(file, sliceIndex, totalPieces, md5, bucketName);
        if (index == -1) {
            mergeFragmentFile(totalPieces, md5,  bucketName,filePath);
        }
        return index;
    }

    /**
     * 上传
     *
     * @param file        文件分片
     * @param sliceIndex  分片索引
     * @param totalPieces 切片总数
     * @param md5         整体文件MD5
     * @param bucketName  存储桶名称
     * @return 返回需要上传的文件序号，-1是上传完成
     */

    @SneakyThrows
    public int uploadFileByFragment(File file, Integer sliceIndex, Integer totalPieces, String md5, String bucketName) {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(md5.concat("/")).build());
        Set<String> objectNames = Sets.newHashSet();
        for (Result<Item> item : results) {
            objectNames.add(item.get().objectName());
        }
        List<Integer> indexList = Stream.iterate(0, i -> ++i).limit(totalPieces).filter(i -> !objectNames.contains(md5.concat("/").concat(Integer.toString(i)))).sorted().collect(Collectors.toList());
        // 返回需要上传的文件序号，-1是上传完成
        if (!indexList.isEmpty()) {
            if (!indexList.contains(sliceIndex)) {
                return indexList.get(0);
            }
        } else {
            return -1;
        }
        // 写入文件
        @Cleanup
        InputStream inputStream = FileUtil.getInputStream(file);
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName)
                // 使用 md5 + 特殊字符 + 索引值 作为分片名称
                .object(md5.concat("/").concat(Integer.toString(sliceIndex))).stream(inputStream, FileUtil.size(file), -1).contentType("application/octet-stream").build());
        if (sliceIndex < totalPieces - 1) {
            return ++sliceIndex;
        } else {
            return -1;
        }

    }

    /**
     * 此方法将多个文件分片合并为一个完整的文件，并验证合并后的文件的 MD5 值。
     */
    public void mergeFragmentFile(Integer totalPieces, String md5, String bucketName,String filePath) throws Exception {
        // 完成上传从缓存目录合并迁移到正式目录
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i).limit(totalPieces).map(i -> ComposeSource.builder().bucket(bucketName).object(md5.concat("/").concat(Integer.toString(i))).build()).collect(Collectors.toList());


        minioClient.composeObject(ComposeObjectArgs.builder().bucket(bucketName).object(filePath).sources(sourceObjectList).build());

        // 删除所有的分片文件
        List<DeleteObject> delObjects = Stream.iterate(0, i -> ++i).limit(totalPieces).map(i -> new DeleteObject(md5.concat("/").concat(Integer.toString(i)))).collect(Collectors.toList());
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(delObjects).build());
        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            log.error("Error in deleting object {}; {}", error.objectName(), error.message());
        }

        log.info("文件: {} 分片合并完成", md5);
    }

}
