package pub.module.customer.biz.controller.cus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.api.entity.BaseEntity;

/**
 * 联络记录 对象
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "联络记录VO")
public class CustomerContactRecordReqVO {
    @Schema(description = "联络方式（1电话、2个人微信、3企业微信）")
    private String contactRecordMethodCode;
    @Schema(description = "联络记录来源（1手工创建、2电访系统、企业微信）")
    private String contactRecordSourceCode;
    @Schema(description = "员工账号")
    private String userCode;
    @Schema(description = "通话时长(联络记录是电访系统时候需要传)")
    private Long contactRecordTalkDuration;
    @Schema(description = "通话文件(联络记录是电访系统时候需要传)")
    private String contactRecordFile;
    @Schema(description = "通话文字(联络记录是电访系统时候需要传)")
    private String contactRecordVoiceText;
    @Schema(description = "通话状态描述")
    private String contactRecordStatusName;
    @Schema(description = "跟踪描述")
    private String contactRecordDescription;
    @Schema(description = "是否有意向")
    private String cusIntentionStatusCode;
    @Schema(description = "意向等级")
    private String cusIntentionLevelCode;
    @Schema(description = "客户编号")
    private String cusCode;
    @Schema(description = "微信号")
    private String cusWechatId;
}
