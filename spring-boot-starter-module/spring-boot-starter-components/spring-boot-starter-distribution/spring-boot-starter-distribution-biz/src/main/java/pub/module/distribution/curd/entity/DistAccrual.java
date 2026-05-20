package pub.module.distribution.curd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dist_accrual")
@Schema(description = "分销分佣计提")
public class DistAccrual extends BaseEntity {

    private String distAccrualCode;
    private String distBizLineCode;
    private String distAccrualSourceTypeCode;
    private String distAccrualSourceId;
    private String distPayerUserCode;
    private String distBeneficiaryUserCode;
    private String distRuleLevelCode;
    private BigDecimal distBaseAmount;
    private BigDecimal distRuleRate;
    private BigDecimal distCommissionAmount;
    private String distAccrualStatusCode;
    private LocalDateTime distSettleAt;
    private String tdOdCode;
    private String tdGdCgyCode;
}
