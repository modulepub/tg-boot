package pub.module.file.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;
import pub.module.file.api.constants.ConfigTypeCodeEnum;

/**
 * CMS-节点 对象
 * @author tg
 * 2026-03-21 21:34:38
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "CMS-节点")
public class BizConfig extends BaseEntity {
                    @Schema(description = "配置编码")
                private String configCode;

                    @Schema(description = "配置名称")
                private String configName;

                    @Schema(description = "配置类型")
                private ConfigTypeCodeEnum configTypeCode;

                    @Schema(description = "是否启用")
                private StatusCodeEnum configEnableStatusCode;

                    @Schema(description = "配置内容")
                private String configContent;


        }
