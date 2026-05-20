package pub.module.distribution.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.distribution.api.constants.DistAccrualStatusCodeEnum;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.constants.DistRefBindStatusCodeEnum;
import pub.module.distribution.api.service.ApiDistPromoteService;
import pub.module.distribution.api.service.dto.DistPromoteInviteeRowDTO;
import pub.module.distribution.api.service.dto.DistPromoteSummaryDTO;
import pub.module.distribution.biz.service.internal.BizWalAccountInternalService;
import pub.module.distribution.curd.entity.DistAccrual;
import pub.module.distribution.curd.entity.DistRefBind;
import pub.module.distribution.curd.entity.WalAccount;
import pub.module.distribution.curd.mapper.DistAccrualMapper;
import pub.module.distribution.curd.mapper.DistRefBindMapper;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApiDistPromoteServiceImpl implements ApiDistPromoteService {

    @Resource
    private DistRefBindMapper distRefBindMapper;
    @Resource
    private DistAccrualMapper distAccrualMapper;
    @Resource
    private BizWalAccountInternalService bizWalAccountInternalService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    public DistPromoteSummaryDTO getSummary(String promoterUserCode, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        WalAccount account = bizWalAccountInternalService.getOrCreate(bizLine, promoterUserCode);
        DistPromoteSummaryDTO dto = new DistPromoteSummaryDTO();
        dto.setWalAvailableBalance(account.getWalAvailableBalance());
        dto.setDistInviteeCount(countInvitees(promoterUserCode, bizLine));

        List<DistAccrual> pending = distAccrualMapper.selectList(new QueryWrapper<DistAccrual>().lambda()
                .eq(DistAccrual::getDistBeneficiaryUserCode, promoterUserCode)
                .eq(DistAccrual::getDistBizLineCode, bizLine)
                .eq(DistAccrual::getDistAccrualStatusCode, DistAccrualStatusCodeEnum.PENDING.getCode()));
        BigDecimal pendingTotal = pending.stream()
                .map(DistAccrual::getDistCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setDistPendingTotalAmount(pendingTotal);
        return dto;
    }

    @Override
    public List<DistPromoteInviteeRowDTO> listInvitees(String promoterUserCode, String distBizLineCode) {
        String bizLine = resolveBizLine(distBizLineCode);
        List<DistRefBind> binds = distRefBindMapper.selectList(new QueryWrapper<DistRefBind>().lambda()
                .eq(DistRefBind::getDistInviterUserCode, promoterUserCode)
                .eq(DistRefBind::getDistBizLineCode, bizLine)
                .eq(DistRefBind::getDistRefBindStatusCode, DistRefBindStatusCodeEnum.VALID.getCode())
                .orderByDesc(DistRefBind::getDistBindTime));
        if (binds.isEmpty()) {
            return List.of();
        }

        List<String> inviteeCodes = binds.stream().map(DistRefBind::getDistInviteeUserCode).toList();
        List<DistAccrual> accruals = distAccrualMapper.selectList(new QueryWrapper<DistAccrual>().lambda()
                .eq(DistAccrual::getDistBeneficiaryUserCode, promoterUserCode)
                .eq(DistAccrual::getDistBizLineCode, bizLine)
                .in(DistAccrual::getDistPayerUserCode, inviteeCodes));

        Map<String, List<DistAccrual>> byPayer = accruals.stream()
                .collect(Collectors.groupingBy(DistAccrual::getDistPayerUserCode));

        List<DistPromoteInviteeRowDTO> rows = new ArrayList<>();
        for (DistRefBind bind : binds) {
            DistPromoteInviteeRowDTO row = new DistPromoteInviteeRowDTO();
            row.setDistInviteeUserCode(bind.getDistInviteeUserCode());
            row.setDistBindTime(bind.getDistBindTime());
            UserDTO user = apiSysUserService.getUserByUserCode(bind.getDistInviteeUserCode());
            row.setUserNickName(user != null ? user.getUserNickName() : bind.getDistInviteeUserCode());

            BigDecimal settled = BigDecimal.ZERO;
            BigDecimal pending = BigDecimal.ZERO;
            for (DistAccrual accrual : byPayer.getOrDefault(bind.getDistInviteeUserCode(), List.of())) {
                if (DistAccrualStatusCodeEnum.SETTLED.getCode().equals(accrual.getDistAccrualStatusCode())) {
                    settled = settled.add(accrual.getDistCommissionAmount());
                } else if (DistAccrualStatusCodeEnum.PENDING.getCode().equals(accrual.getDistAccrualStatusCode())) {
                    pending = pending.add(accrual.getDistCommissionAmount());
                }
            }
            row.setDistSettleableAmount(settled);
            row.setDistInServiceAmount(pending);
            rows.add(row);
        }
        return rows;
    }

    private long countInvitees(String promoterUserCode, String bizLine) {
        return distRefBindMapper.selectCount(new QueryWrapper<DistRefBind>().lambda()
                .eq(DistRefBind::getDistInviterUserCode, promoterUserCode)
                .eq(DistRefBind::getDistBizLineCode, bizLine)
                .eq(DistRefBind::getDistRefBindStatusCode, DistRefBindStatusCodeEnum.VALID.getCode()));
    }

    private String resolveBizLine(String distBizLineCode) {
        return StrUtil.blankToDefault(distBizLineCode, DistBizLineCodeEnum.DATING.getCode());
    }
}
