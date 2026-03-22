package pub.module.config.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.data.api.entity.BaseEntity;

/**
 * CMS-节点 对象
 * @author tg
 * 2026-03-21 21:34:38
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "CMS-节点")
public class BizConfig extends BaseEntity {
                    /** 配置编码 */
                        @Schema(description = "配置编码")
                private String configCode;

                    /** 配置名称 */
                        @Schema(description = "配置名称")
                private String configName;

                    /** 配置类型 */
                        @Schema(description = "配置类型")
                private String configTypeCode;

                    /** 是否启用 */
                        @Schema(description = "是否启用")
                private String configEnableStatusCode;

                    /** 配置内容 */
                        @Schema(description = "配置内容")
                private String configContent;


        }
