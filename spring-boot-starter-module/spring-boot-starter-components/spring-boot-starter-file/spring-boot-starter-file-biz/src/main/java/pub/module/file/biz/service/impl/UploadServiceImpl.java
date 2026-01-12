package pub.module.file.biz.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import pub.module.file.BizFileAutoConfiguration;
import pub.module.file.api.service.BizUploadService;
import pub.module.file.biz.constants.UploadTypeEnum;
import pub.module.file.biz.service.AliOSSService;
import org.springframework.stereotype.Service;
import pub.module.file.biz.service.LocalUploadService;
import pub.module.file.biz.service.MinioUploadService;
import pub.module.file.biz.utils.BizFileUtil;

import jakarta.annotation.Resource;
import java.io.File;

/**
 * 说明：
 * 1、文件校验是框架做的事情。在application.yml中可以配置。
 * 2、获取文件后缀，使用hutool的FileUtil就行，不必自己写方法。
 */
@Service
@Slf4j
public class UploadServiceImpl implements BizUploadService {
    @Resource
    AliOSSService aliOSSService;
    @Resource
    BizFileAutoConfiguration bizFileAutoConfiguration;
    @Resource
    MinioUploadService minioUploadService;
    @Resource
    LocalUploadService localUploadService;


    public int uploadByFragment(File file, String filePath, String sliceFileMd5, Integer sliceIndex, Integer totalPieces) {

        int index = 0;
        if (UploadTypeEnum.MINIO.getCode().equals(bizFileAutoConfiguration.getUploadType())) {
            index = minioUploadService.uploadByFragment(file, sliceFileMd5, sliceIndex, totalPieces, filePath);
        } else if (UploadTypeEnum.LOCAL.getCode().equals(bizFileAutoConfiguration.getUploadType())) {
            index = localUploadService.uploadByFragment(file, sliceFileMd5, sliceIndex, totalPieces, filePath);
        } else if (UploadTypeEnum.ALI_OSS.getCode().equals(bizFileAutoConfiguration.getUploadType())) {
            index = aliOSSService.uploadChunk(file, sliceFileMd5, sliceIndex, FileUtil.size(file), totalPieces, filePath);
        }
        return index;
    }



    /**
     * @param file 文件
     * @param biz 业务路劲
     * @return 文件服务器路径
     */
    @Override
    public String upload(File file, String biz) {
        String filePath = BizFileUtil.getPath(RandomUtil.randomString(10), file.getName(), biz);
        this.uploadByFragment(file, filePath, RandomUtil.randomString(10), 0, 1);
        return filePath;
    }

    /**
     * @param bytes     文件字节数组
     * @param fileName  文件名
     * @param biz       业务路径
     * @return 文件服务器路径
     */
    @Override
    public String upload(byte[] bytes, String fileName, String biz) {
        String filePath = BizFileUtil.getPath(RandomUtil.randomString(10), fileName, biz);
        this.uploadByFragment(FileUtil.writeBytes(bytes,FileUtil.createTempFile()), filePath, RandomUtil.randomString(10), 0, 1);
        return filePath;
    }

}
