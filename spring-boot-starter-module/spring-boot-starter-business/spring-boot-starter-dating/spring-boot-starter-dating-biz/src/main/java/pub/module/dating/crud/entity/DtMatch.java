package pub.module.dating.crud.entity;

import pub.module.dating.api.constants.MatchRelationProgressCodeEnum;
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
    @Schema(description = "所属部门")
    private String sysOrgCode;

    @Schema(description = "匹配申请编码")
    private String mtCode;

    @Schema(description = "匹配申请名称")
    private String mtName;

    @Schema(description = "红娘用户编码")
    private String mtMkCode;

    @Schema(description = "追求者")
    private String mtPursuingCusCode;
    @Schema(description = "追求者姓名")
    private String mtPursuingCusName;
    @Schema(description = "追求者头像")
    private String mtPursuingCusAvatar;

    @Schema(description = "被追求者")
    private String mtPursuedCusCode;



    @Schema(description = "被追求者姓名")
    private String mtPursuedCusName;

    @Schema(description = "被追求者头像")
    private String mtPursuedCusAvatar;

    @Schema(description = "关系进度：pendingCommunication待沟通 communicating沟通中 meetingScheduled已约见 developing关系发展中 ended已结束")
    private MatchRelationProgressCodeEnum mtRelationProgressCode;

    @Schema(description = "见面截图，逗号分隔URL")
    private String mtMeetingScreenshot;

    @Schema(description = "聊天截图，逗号分隔URL")
    private String mtChatScreenshot;


}
