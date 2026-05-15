package pub.module.customer.curd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 联络记录 对象
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "联络记录")
public class CustomerContactRecord extends BaseEntity {
    private String contactRecordCode;
    @Schema(description = "员工账号")
    private String userCode;
    @Schema(description = "员工姓名")
    private String userRealName;
    @Schema(description = "联络方式（1电话、2个人微信、3企业微信）")
    private String contactRecordMethodCode;
    @Schema(description = "联络记录来源（1手工创建、2电访系统、企业微信）")
    private String contactRecordSourceCode;
    @Schema(description = "通话时长")
    private Long contactRecordTalkDuration;
    @Schema(description = "通话文件")
    private String contactRecordFile;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "通话时间")
    private LocalDateTime contactRecordTime;
    @Schema(description = "通话文字")
    private String contactRecordVoiceText;
    @Schema(description = "跟踪描述")
    private String contactRecordDescription;
    @Schema(description = "是否意向")
    private String cusIntentionStatusCode;
    @Schema(description = "客户编号")
    private String cusCode;
    @Schema(description = "客户姓名")
    private String cusName;
    @Schema(description = "手机号")
    private String cusPhone;
    @Schema(description = "微信号")
    private String cusWechatId;
    @Schema(description = "意向等级")
    private String cusIntentionLevelCode;

}
