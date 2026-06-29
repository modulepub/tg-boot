package pub.module.common.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tg.actuator")
public class TgActuatorSecurityProperties {

    private boolean enabled = true;

    private String username = "actuator";

    private String password = "change-me";
}
