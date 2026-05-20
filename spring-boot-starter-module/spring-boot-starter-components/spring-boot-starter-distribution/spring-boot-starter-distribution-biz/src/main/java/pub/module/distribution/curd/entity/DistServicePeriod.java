package pub.module.distribution.curd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dist_service_period")
@Schema(description = "分销服务期")
public class DistServicePeriod extends BaseEntity {

    private String distServicePeriodCode;
    private String distAccrualSourceId;
    private LocalDateTime distPeriodStartAt;
    private LocalDateTime distPeriodEndAt;
    private String distPeriodStatusCode;
}
