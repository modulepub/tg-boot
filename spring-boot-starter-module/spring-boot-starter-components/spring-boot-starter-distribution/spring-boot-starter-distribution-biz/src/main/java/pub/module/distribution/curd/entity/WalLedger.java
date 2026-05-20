package pub.module.distribution.curd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wal_ledger")
@Schema(description = "分销钱包流水")
public class WalLedger extends BaseEntity {

    private String walLedgerCode;
    private String walAccountCode;
    private String walLedgerTypeCode;
    private BigDecimal walChangeAmount;
    private BigDecimal walBalanceAfter;
    private String distAccrualCode;
    private String walWithdrawCode;
    private String walLedgerRemark;
}
