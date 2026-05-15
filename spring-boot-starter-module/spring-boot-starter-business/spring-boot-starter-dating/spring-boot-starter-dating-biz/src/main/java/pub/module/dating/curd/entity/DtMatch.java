package pub.module.dating.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 匹配申请（牵线） 对象
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "匹配申请（牵线）")
public class DtMatch extends BaseEntity {
    /**
     * 所属部门
     */
    @Schema(description = "所属部门")
    private String sysOrgCode;

    /**
     * 匹配申请编码
     */
    @Schema(description = "匹配申请编码")
    private String mtCode;

    /**
     * 匹配申请名称
     */
    @Schema(description = "匹配申请名称")
    private String mtName;

    /**
     * 红娘用户编码
     */
    @Schema(description = "红娘用户编码")
    private String mtMkCode;

    /**
     * 追求者
     */
    @Schema(description = "追求者")
    private String mtPursuingCusCode;
    @Schema(description = "追求者姓名")
    private String mtPursuingCusName;
    @Schema(description = "追求者头像")
    private String mtPursuingCusAvatar;

    /**
     * 被追求者
     */
    @Schema(description = "被追求者")
    private String mtPursuedCusCode;



    @Schema(description = "被追求者姓名")
    private String mtPursuedCusName;

    @Schema(description = "被追求者头像")
    private String mtPursuedCusAvatar;

    /**
     * 是否通过
     */
    @Schema(description = "是否通过（null 沟通中 0牵线失败 1 牵线成功）")
    private String mtPassedStatusCode;


}
