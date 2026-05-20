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
@TableName("wal_withdraw")
@Schema(description = "分销提现申请")
public class WalWithdraw extends BaseEntity {

    private String walWithdrawCode;
    private String distBizLineCode;
    private String walUserCode;
    private BigDecimal walWithdrawAmount;
    private String walWithdrawBankCode;
    private String walWithdrawCardNo;
    private String walWithdrawStatusCode;
    private LocalDateTime walWithdrawArrivedTime;
}
