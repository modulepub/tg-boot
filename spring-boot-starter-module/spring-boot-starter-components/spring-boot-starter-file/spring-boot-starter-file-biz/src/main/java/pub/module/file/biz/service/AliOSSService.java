package pub.module.file.biz.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import pub.module.file.api.service.ApiConfigService;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class AliOSSService {

    @Resource
    ApiConfigService apiConfigService;
    final String CONFIG_CODE = "fileConfig";
    String bucketName;
    private final ConcurrentHashMap<String, Oss> lock = new ConcurrentHashMap<>();


    public OSSClient getOssClient() {
        JSONObject root = apiConfigService.getConfigByCode(CONFIG_CODE);
        JSONObject jsonObject = root.getJSONObject("aliOss");
        bucketName = jsonObject.getStr("bucketName");
        return new OSSClient(jsonObject.getStr("endpoint"),
                new DefaultCredentialProvider(jsonObject.getStr("accessKey"), jsonObject.getStr("secretKey")),
                new ClientConfiguration());
    }

    @Data
    public static class Oss {
        String uploadId;
        List<PartETag> partETagList;
    }

    /**
     * 上传分片文件
     *
     * @param uploadId   上传id
     * @param key        key
     * @param chunkIndex 分片索引
     * @param chunkSize  分片大小
     * @param chunkCount x
     */
    private PartETag uploadChunkPart(String uploadId, String key, InputStream inputStream,
                                     Integer chunkIndex, long chunkSize, Integer chunkCount) {
        UploadPartRequest partRequest = new UploadPartRequest();
        // 阿里云 oss 文件根目录
        partRequest.setBucketName(bucketName);
        // 文件key
        partRequest.setKey(key);
        // 分片上传uploadId
        partRequest.setUploadId(uploadId);
        // 分片文件
        partRequest.setInputStream(inputStream);
        // 分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
        partRequest.setPartSize(chunkSize);
        // 分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
        partRequest.setPartNumber(chunkIndex);
        // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
        UploadPartResult uploadPartResult = this.getOssClient().uploadPart(partRequest);
        // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在redis中。
        return uploadPartResult.getPartETag();
    }

    public Boolean checkExist(String key) {
        return this.getOssClient().doesObjectExist(bucketName, key);
    }

    /**
     * 文件合并
     *
     * @param uploadId  上传id
     * @param key       key
     * @param chunkTags 分片上传信息
     */
    private CompleteMultipartUploadResult uploadChunkComplete(String uploadId, String key, List<PartETag> chunkTags) {
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, key, uploadId, chunkTags);
        return this.getOssClient().completeMultipartUpload(completeMultipartUploadRequest);
    }

    /**
     * 初始化上传id uploadId
     *
     * @param key key
     */
    private String uploadChunkInit(String key) {
        // 创建分片上传对象
        InitiateMultipartUploadRequest uploadRequest = new InitiateMultipartUploadRequest(bucketName, key);
        // 初始化分片
        InitiateMultipartUploadResult result = this.getOssClient().initiateMultipartUpload(uploadRequest);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        return result.getUploadId();
    }

    /**
     * 分片上传
     * 1、检查文件是否上传
     * 2、检查文件是否第一次上传，第一次上传创建上传id uploadId
     * 3、检查是否是断点续传，如果是返回已上传的分片
     * 4、分片上传到阿里云OSS上，并记录上传信息到Redis
     * 5、判断是否已上传完成，已完成：合并所有分片为源文件
     *
     * @param md5        上传id
     * @param filePath   文件在OSS上的key
     * @param file       文件分片
     * @param chunkIndex 分片索引
     * @param chunkSize  分片大小
     * @param chunkCount 总分片数
     */
    @SneakyThrows
    public int uploadChunk(File file, String md5, Integer chunkIndex,
                           long chunkSize, Integer chunkCount, String filePath) {
        filePath = filePath.substring(filePath.indexOf("/") + 1);
        chunkIndex++;
        // 判断是否上传
        if (checkExist(filePath)) {
            return -1;
        }
        Oss oss = lock.get(filePath);
        // 判断是否第一次上传
        if (oss == null) {
            String uploadId = uploadChunkInit(filePath);
            oss = new Oss();
            oss.setUploadId(uploadId);
            oss.setPartETagList(new ArrayList<>());
            lock.put(filePath, oss);
        }

        // 上传分片
        PartETag partETag = uploadChunkPart(oss.getUploadId(), filePath, FileUtil.getInputStream(file), chunkIndex, chunkSize, chunkCount);
        log.info("完成分片：{}", partETag);
        List<PartETag> uploadedCache = oss.getPartETagList();
        // 分片上传完成缓存key
        uploadedCache.add(partETag);
        // 取出所有已上传的分片信息
        // 判断是否上传完成
        if (uploadedCache.size() == chunkCount) {
            CompleteMultipartUploadResult completeMultipartUploadResult = uploadChunkComplete(oss.getUploadId(), filePath, uploadedCache);
            log.info("ALL_OSS已经上传完成:{}", completeMultipartUploadResult.getKey());
            return -1;
        } else {
            return partETag.getPartNumber();
        }

    }


    public String upload(InputStream inputStream, String fileName, String fileDir) throws Exception {
        //判断桶是否存在,不存在则创建桶
        if (!this.getOssClient().doesBucketExist(bucketName)) {
            this.getOssClient().createBucket(bucketName);
        }
        StringBuilder filePath = new StringBuilder();
        // 获取文件名
        filePath.append(fileDir).append("/").append(fileName);
        PutObjectResult result = this.getOssClient().putObject(bucketName, filePath.toString(), inputStream);
        log.info("上传结果：{}", result);
        return "/" + filePath;
    }


}