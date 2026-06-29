package pub.module.distribution.api.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户账单汇总")
public class DistUserBillSummaryDTO {

    @Schema(description = "主键 id")
    private String id;

    @Schema(description = "创建人")
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "所属组织")
    private String orgCode;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "序号")
    private Long seqNo;

    @Schema(description = "逻辑删除标识：0-未删除，1-已删除")
    private Integer deleted;

    @Schema(description = "汇总流水编码")
    private String distUserBillSummaryCode;

    @Schema(description = "用户编码（账单所属用户）")
    private String distUserCode;

    @Schema(description = "用户昵称（冗余）")
    private String distUserNickName;

    @Schema(description = "用户真实姓名（冗余）")
    private String distUserRealName;

    @Schema(description = "邀请人用户编码（冗余）")
    private String distInviterUserCode;

    @Schema(description = "邀请人昵称（冗余）")
    private String distInviterUserNickName;

    @Schema(description = "邀请人真实姓名（冗余）")
    private String distInviterUserRealName;

    @Schema(description = "业务线编码")
    private String distBizLineCode;

    @Schema(description = "付费总金额（本人消费）")
    private BigDecimal distPaidTotalAmount;

    @Schema(description = "服务期内总金额（待结算佣金）")
    private BigDecimal distInServiceTotalAmount;

    @Schema(description = "子级用户付费总金额")
    private BigDecimal distSubPaidTotalAmount;

    @Schema(description = "子级服务期内总金额")
    private BigDecimal distSubInServiceTotalAmount;
}
