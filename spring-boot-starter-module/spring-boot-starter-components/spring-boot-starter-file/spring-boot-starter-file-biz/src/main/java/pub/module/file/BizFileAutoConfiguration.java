package pub.module.file;

import io.minio.MinioClient;
import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@ComponentScan(basePackages = {"pub.module.file.**"})
@Configuration
public class BizFileAutoConfiguration {
    @Value("${upload.minio.bucketName}")
    String minioBucketName = "";
    @Value("${upload.minio.minioUrl}")
    String minioUrl;
    @Value("${upload.minio.minioName}")
    String minioName;
    @Value("${upload.minio.minioPass}")
    String minioPass;
    @Value("${upload.urlPrefix}")
    String urlPrefix;
    @Value("${upload.type}")
    String uploadType = "";
    @Value("${upload.local.path}")
    String path;
    @Value("${upload.aliOss.endpoint}")
     String ossEndPoint;
    @Value("${upload.aliOss.accessKey}")
     String ossAccessKeyId;
    @Value("${upload.aliOss.secretKey}")
     String ossAccessKeySecret;
    @Value("${upload.aliOss.bucketName}")
     String ossBucketName;

    @Bean
    @ConditionalOnMissingBean
    public MinioClient init() {
        return MinioClient.builder().endpoint(minioUrl).credentials(minioName, minioPass).build();
    }
}
