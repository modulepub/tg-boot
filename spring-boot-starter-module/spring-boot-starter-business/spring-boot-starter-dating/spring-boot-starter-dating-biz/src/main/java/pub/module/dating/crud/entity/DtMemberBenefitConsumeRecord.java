package pub.module.dating.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 会员权益消费记录
 */
@Data
@TableName("dt_member_benefit_consume_record")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "会员权益消费记录")
public class DtMemberBenefitConsumeRecord extends BaseEntity {

    @Schema(description = "权益消费记录编号")
    private String cusMbCstRecordCode;

    @Schema(description = "系统用户编码")
    private String userCode;

    @Schema(description = "客户编号")
    private String cusCode;

    @Schema(description = "权益类型：addFriend/recommend/match")
    private String benefitTypeCode;

    @Schema(description = "消费次数")
    private Long consumeAmount;

    @Schema(description = "业务关联编码")
    private String bizRef;
}
