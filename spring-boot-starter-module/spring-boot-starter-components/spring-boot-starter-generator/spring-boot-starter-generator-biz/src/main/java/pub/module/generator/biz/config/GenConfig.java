package pub.module.generator.biz.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
public class GenConfig
{
    /** 作者 */
    public  String author;

    /** 生成包路径 */
    public  String packageName;

    /** 模块名称 */
    public  String moduleName;


    /** 是否允许生成文件覆盖到本地（自定义路径） */

    public  Boolean allowOverwrite;

}
