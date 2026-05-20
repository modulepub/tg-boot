package pub.module.distribution.biz.service.internal;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.distribution.api.constants.WalLedgerTypeCodeEnum;
import pub.module.distribution.curd.entity.WalAccount;
import pub.module.distribution.curd.entity.WalLedger;
import pub.module.distribution.curd.mapper.WalAccountMapper;
import pub.module.distribution.curd.mapper.WalLedgerMapper;

import java.math.BigDecimal;

@Service
public class BizWalAccountInternalService {

    @Resource
    private WalAccountMapper walAccountMapper;
    @Resource
    private WalLedgerMapper walLedgerMapper;

    @Transactional(rollbackFor = Exception.class)
    public WalAccount getOrCreate(String distBizLineCode, String walUserCode) {
        WalAccount account = walAccountMapper.selectOne(new QueryWrapper<WalAccount>().lambda()
                .eq(WalAccount::getDistBizLineCode, distBizLineCode)
                .eq(WalAccount::getWalUserCode, walUserCode), false);
        if (account != null) {
            return account;
        }
        account = new WalAccount();
        account.setId(IdUtil.getSnowflakeNextIdStr());
        account.setWalAccountCode(IdUtil.getSnowflakeNextIdStr());
        account.setDistBizLineCode(distBizLineCode);
        account.setWalUserCode(walUserCode);
        account.setWalAvailableBalance(BigDecimal.ZERO);
        account.setWalFrozenBalance(BigDecimal.ZERO);
        account.setWalTotalEarned(BigDecimal.ZERO);
        account.setWalTotalWithdrawn(BigDecimal.ZERO);
        walAccountMapper.insert(account);
        return account;
    }

    @Transactional(rollbackFor = Exception.class)
    public void creditSettledCommission(WalAccount account, BigDecimal amount, String distAccrualCode) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal newAvailable = account.getWalAvailableBalance().add(amount);
        BigDecimal newEarned = account.getWalTotalEarned().add(amount);
        account.setWalAvailableBalance(newAvailable);
        account.setWalTotalEarned(newEarned);
        walAccountMapper.updateById(account);
        insertLedger(account, WalLedgerTypeCodeEnum.ACCRUAL_SETTLE.getCode(), amount, newAvailable, distAccrualCode, null,
                "佣金结算入账");
    }

    @Transactional(rollbackFor = Exception.class)
    public void freezeForWithdraw(WalAccount account, BigDecimal amount, String walWithdrawCode) {
        BigDecimal newAvailable = account.getWalAvailableBalance().subtract(amount);
        BigDecimal newFrozen = account.getWalFrozenBalance().add(amount);
        account.setWalAvailableBalance(newAvailable);
        account.setWalFrozenBalance(newFrozen);
        walAccountMapper.updateById(account);
        insertLedger(account, WalLedgerTypeCodeEnum.WITHDRAW_FREEZE.getCode(), amount.negate(), newAvailable, null,
                walWithdrawCode, "提现冻结");
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeWithdraw(WalAccount account, BigDecimal amount, String walWithdrawCode) {
        BigDecimal newFrozen = account.getWalFrozenBalance().subtract(amount);
        BigDecimal newWithdrawn = account.getWalTotalWithdrawn().add(amount);
        account.setWalFrozenBalance(newFrozen);
        account.setWalTotalWithdrawn(newWithdrawn);
        walAccountMapper.updateById(account);
        insertLedger(account, WalLedgerTypeCodeEnum.WITHDRAW_SUCCESS.getCode(), BigDecimal.ZERO,
                account.getWalAvailableBalance(), null, walWithdrawCode, "提现成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public void rejectWithdraw(WalAccount account, BigDecimal amount, String walWithdrawCode) {
        BigDecimal newAvailable = account.getWalAvailableBalance().add(amount);
        BigDecimal newFrozen = account.getWalFrozenBalance().subtract(amount);
        account.setWalAvailableBalance(newAvailable);
        account.setWalFrozenBalance(newFrozen);
        walAccountMapper.updateById(account);
        insertLedger(account, WalLedgerTypeCodeEnum.WITHDRAW_REJECT.getCode(), amount, newAvailable, null,
                walWithdrawCode, "提现驳回退回");
    }

    private void insertLedger(WalAccount account, String typeCode, BigDecimal changeAmount, BigDecimal balanceAfter,
            String distAccrualCode, String walWithdrawCode, String remark) {
        WalLedger ledger = new WalLedger();
        ledger.setId(IdUtil.getSnowflakeNextIdStr());
        ledger.setWalLedgerCode(IdUtil.getSnowflakeNextIdStr());
        ledger.setWalAccountCode(account.getWalAccountCode());
        ledger.setWalLedgerTypeCode(typeCode);
        ledger.setWalChangeAmount(changeAmount);
        ledger.setWalBalanceAfter(balanceAfter);
        ledger.setDistAccrualCode(distAccrualCode);
        ledger.setWalWithdrawCode(walWithdrawCode);
        ledger.setWalLedgerRemark(remark);
        walLedgerMapper.insert(ledger);
    }
}
