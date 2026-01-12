package pub.module.generator.biz.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取代码生成相关配置
 * 
 * @author ruoyi
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gen")
@PropertySource(value = { "classpath:generator.yml" })
public class GenConfig
{
    /** 作者 */
    @Value("${author}")
    public  String author;

    /** 生成包路径 */
    @Value("${packageName}")
    public  String packageName;

    /** 模块名称 */
    @Value("${moduleName}")
    public  String moduleName;

    /** 自动去除表前缀 */
    @Value("${autoRemovePre}")
    public  Boolean autoRemovePre;

    /** 表前缀 */
    @Value("${tablePrefix}")
    public  String tablePrefix;

    /** 是否允许生成文件覆盖到本地（自定义路径） */

    @Value("${allowOverwrite}")
    public  Boolean allowOverwrite;

}
