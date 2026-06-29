package pub.module.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "tg.security")
public class TgSecurityProperties {

    /**
     * 额外放行路径（在内置 swagger/静态资源之外追加）。
     */
    private List<String> extraPermitAll = new ArrayList<>();
}
