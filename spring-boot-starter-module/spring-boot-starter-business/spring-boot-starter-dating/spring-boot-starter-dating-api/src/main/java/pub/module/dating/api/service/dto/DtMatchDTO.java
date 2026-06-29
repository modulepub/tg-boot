package pub.module.dating.api.service.dto;

import pub.module.dating.api.constants.MatchRelationProgressCodeEnum;
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

    @Schema(description = "追求者手机号")
    private String mtPursuingCusPhone;

    @Schema(description = "被追求者手机号")
    private String mtPursuedCusPhone;

    @Schema(description = "申请双方是否已是好友关系")
    private Boolean mtAreFriends;

    @Schema(description = "关系进度")
    private MatchRelationProgressCodeEnum mtRelationProgressCode;

    @Schema(description = "见面截图，逗号分隔URL")
    private String mtMeetingScreenshot;

    @Schema(description = "聊天截图，逗号分隔URL")
    private String mtChatScreenshot;
}
