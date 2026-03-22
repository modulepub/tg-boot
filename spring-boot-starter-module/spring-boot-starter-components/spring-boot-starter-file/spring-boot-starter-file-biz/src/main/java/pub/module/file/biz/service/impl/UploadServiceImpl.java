package pub.module.file.biz.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import pub.module.config.api.service.ApiConfigService;
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
    MinioUploadService minioUploadService;
    @Resource
    LocalUploadService localUploadService;
    @Resource
    ApiConfigService apiConfigService;
    final String CONFIG_CODE = "fileConfig";
    public JSONObject getConfig(){
        JSONObject jsonObject = apiConfigService.getConfigByCode(CONFIG_CODE);
        if(jsonObject==null){
            jsonObject = new JSONObject();
            jsonObject.set("urlPrefix","https://matchlove.oss-cn-beijing.aliyuncs.com");
            jsonObject.set("typeDesc","文件上传方式，可选项：local（本地）、minio（minio）、aliOss（阿里云OSS）");
            jsonObject.set("type","aliOss");
            JSONObject local = new JSONObject();
            jsonObject.set("local",local);
            local.set("storePath","/opt/upFiles");
            JSONObject minio = new JSONObject();
            jsonObject.set("minio",minio);
            minio.set("minioUrl","http://192.168.0.1:9090");
            minio.set("minioName","minioadmin");
            minio.set("minioPass","minioadmin");
            minio.set("bucketName","minioadmin");
            JSONObject aliOss = new JSONObject();
            jsonObject.set("aliOss",aliOss);
            aliOss.set("endpoint","oss-cn-beijing.aliyuncs.com");
            aliOss.set("accessKey","LTAI5tAX24LHf3PQEcSiQgNA");
            aliOss.set("secretKey","c9ExQbzp0ia6E95LlJiT7DUyf91SJa");
            aliOss.set("bucketName","matchlove");
            apiConfigService.updateConfigByCode(CONFIG_CODE,jsonObject);
        }
        return jsonObject;
    }


    public int uploadByFragment(File file, String filePath, String sliceFileMd5, Integer sliceIndex, Integer totalPieces) {
        JSONObject jsonObject = this.getConfig();
        int index = 0;
        if (UploadTypeEnum.MINIO.getCode().equals(jsonObject.getStr("type"))) {
            index = minioUploadService.uploadByFragment(file, sliceFileMd5, sliceIndex, totalPieces, filePath);
        } else if (UploadTypeEnum.LOCAL.getCode().equals(jsonObject.getStr("type"))) {
            index = localUploadService.uploadByFragment(file, sliceFileMd5, sliceIndex, totalPieces, filePath);
        } else if (UploadTypeEnum.ALI_OSS.getCode().equals(jsonObject.getStr("type"))) {
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
