package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 匹配申请（牵线）传输对象，供 API 层与前端交互；与持久化实体字段对齐。
 *
 * @author tg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "匹配申请（牵线）DTO")
public class DtMatchDTO extends BaseEntity {

    @Schema(description = "所属部门")
    private String sysOrgCode;

    @Schema(description = "匹配申请编码")
    private String mtCode;

    @Schema(description = "匹配申请名称")
    private String mtName;

    @Schema(description = "红娘编码")
    private String mtMkCode;

    @Schema(description = "追求者客户编码")
    private String mtPursuingCusCode;

    @Schema(description = "追求者姓名")
    private String mtPursuingCusName;

    @Schema(description = "追求者头像")
    private String mtPursuingCusAvatar;

    @Schema(description = "被追求者客户编码")
    private String mtPursuedCusCode;

    @Schema(description = "被追求者姓名")
    private String mtPursuedCusName;

    @Schema(description = "被追求者头像")
    private String mtPursuedCusAvatar;

    @Schema(description = "是否通过（null 沟通中 0牵线失败 1 牵线成功）")
    private String mtPassedStatusCode;
}
