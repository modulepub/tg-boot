package pub.module.system.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "公开-APP配置")
public class SysAppConfigPubDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置 key")
    private String appConfigKey;

    @Schema(description = "配置值（JSON 对象）")
    private Map<String, Object> appConfigValue;
}
