package pub.module.distribution.curd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dist_rule")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "分销分佣规则")
public class DistRule implements Serializable {

    @TableId(value = "dist_rule_code", type = IdType.INPUT)
    private String distRuleCode;

    private String distBizLineCode;
    private String distRuleName;
    private String distRuleLevelCode;
    private String distPromoterRoleCode;
    private BigDecimal distRuleRate;
    private String distSettleModeCode;
    private String distProductScopeJson;
    private String distRuleEnabledCode;
    private String distRuleRemark;

    private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private String orgCode;
    private String version;
    private Long seqNo;
    @TableLogic
    private Integer deleted;
}
