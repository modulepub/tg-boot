package pub.module.distribution.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.constants.WalWithdrawStatusCodeEnum;
import pub.module.distribution.api.service.ApiDistWalletService;
import pub.module.distribution.api.service.dto.WalWithdrawApplyDTO;
import pub.module.distribution.api.service.dto.WalWithdrawRecordDTO;
import pub.module.distribution.biz.service.internal.BizWalAccountInternalService;
import pub.module.distribution.curd.entity.WalAccount;
import pub.module.distribution.curd.entity.WalWithdraw;
import pub.module.distribution.curd.mapper.WalWithdrawMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiDistWalletServiceImpl implements ApiDistWalletService {

    private static final List<String> ALLOWED_BANKS = List.of("icbc", "abc", "boc", "ccb");

    @Resource
    private WalWithdrawMapper walWithdrawMapper;
    @Resource
    private BizWalAccountInternalService bizWalAccountInternalService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyWithdraw(String userCode, String distBizLineCode, WalWithdrawApplyDTO dto) {
        Assert.notNull(dto, "提现参数不能为空");
        Assert.notBlank(userCode, "用户编码不能为空");
        String bizLine = StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
        BigDecimal amount = dto.getWalWithdrawAmount();
        Assert.notNull(amount, "提现金额不能为空");
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0, "提现金额必须大于0");
        String bankCode = StrUtil.trim(dto.getWalWithdrawBankCode());
        Assert.isTrue(ALLOWED_BANKS.contains(bankCode), "仅支持四大行储蓄卡");
        String cardNo = StrUtil.trim(dto.getWalWithdrawCardNo()).replaceAll("\\D", "");
        Assert.isTrue(cardNo.length() >= 16, "请填写有效银行卡号");

        WalAccount account = bizWalAccountInternalService.getOrCreate(bizLine, userCode);
        Assert.isTrue(account.getWalAvailableBalance().compareTo(amount) >= 0, "可提现余额不足");

        WalWithdraw withdraw = new WalWithdraw();
        withdraw.setId(IdUtil.getSnowflakeNextIdStr());
        withdraw.setWalWithdrawCode(IdUtil.getSnowflakeNextIdStr());
        withdraw.setDistBizLineCode(bizLine);
        withdraw.setWalUserCode(userCode);
        withdraw.setWalWithdrawAmount(amount);
        withdraw.setWalWithdrawBankCode(bankCode);
        withdraw.setWalWithdrawCardNo(cardNo);
        withdraw.setWalWithdrawStatusCode(WalWithdrawStatusCodeEnum.PENDING.getCode());
        walWithdrawMapper.insert(withdraw);
        bizWalAccountInternalService.freezeForWithdraw(account, amount, withdraw.getWalWithdrawCode());
    }

    @Override
    public List<WalWithdrawRecordDTO> listWithdrawRecords(String userCode, String distBizLineCode) {
        String bizLine = StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
        List<WalWithdraw> list = walWithdrawMapper.selectList(new QueryWrapper<WalWithdraw>().lambda()
                .eq(WalWithdraw::getWalUserCode, userCode)
                .eq(WalWithdraw::getDistBizLineCode, bizLine)
                .orderByDesc(WalWithdraw::getCreateTime));
        return list.stream().map(this::toRecordDto).collect(Collectors.toList());
    }

    private WalWithdrawRecordDTO toRecordDto(WalWithdraw w) {
        WalWithdrawRecordDTO dto = new WalWithdrawRecordDTO();
        dto.setWalWithdrawCode(w.getWalWithdrawCode());
        dto.setWalWithdrawBankCode(w.getWalWithdrawBankCode());
        dto.setWalWithdrawCardDisplay(maskCard(w.getWalWithdrawBankCode(), w.getWalWithdrawCardNo()));
        dto.setWalWithdrawAmount(w.getWalWithdrawAmount());
        dto.setWalWithdrawStatusCode(w.getWalWithdrawStatusCode());
        dto.setCreateTime(w.getCreateTime());
        dto.setWalWithdrawArrivedTime(w.getWalWithdrawArrivedTime());
        return dto;
    }

    private String maskCard(String bankCode, String cardNo) {
        String bankName = switch (bankCode) {
            case "icbc" -> "工商银行";
            case "abc" -> "农业银行";
            case "boc" -> "中国银行";
            case "ccb" -> "建设银行";
            default -> "银行卡";
        };
        if (StrUtil.isBlank(cardNo) || cardNo.length() < 8) {
            return bankName;
        }
        String tail = cardNo.substring(cardNo.length() - 4);
        return bankName + " · **** **** " + tail;
    }
}
