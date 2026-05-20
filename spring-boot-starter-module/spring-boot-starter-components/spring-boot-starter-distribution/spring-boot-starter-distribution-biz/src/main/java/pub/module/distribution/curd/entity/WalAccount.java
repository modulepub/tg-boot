package pub.module.distribution.curd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wal_account")
@Schema(description = "分销钱包账户")
public class WalAccount extends BaseEntity {

    private String walAccountCode;
    private String distBizLineCode;
    private String walUserCode;
    private BigDecimal walAvailableBalance;
    private BigDecimal walFrozenBalance;
    private BigDecimal walTotalEarned;
    private BigDecimal walTotalWithdrawn;
}
