package pub.module.system.crud.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "APP配置")
public class SysAppConfig extends BaseEntity {

    @Schema(description = "配置 key")
    private String appConfigKey;

    @Schema(description = "配置值（JSON）")
    private String appConfigValue;
}
