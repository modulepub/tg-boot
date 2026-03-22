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
@MapperScan(basePackages = {"pub.module.file.**.mapper"})
@Configuration
public class BizFileAutoConfiguration {

}
