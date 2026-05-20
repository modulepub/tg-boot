package pub.module.distribution.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.distribution.api.constants.*;
import pub.module.distribution.api.service.ApiDistCommissionService;
import pub.module.distribution.api.service.SpiDistPromoterRoleResolver;
import pub.module.distribution.api.service.dto.DistOrderPaidNotifyDTO;
import pub.module.distribution.biz.service.internal.BizWalAccountInternalService;
import pub.module.distribution.biz.util.DistPeriodParseUtil;
import pub.module.distribution.curd.entity.*;
import pub.module.distribution.curd.mapper.*;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class ApiDistCommissionServiceImpl implements ApiDistCommissionService {

    private static final String DEFAULT_BIZ_LINE = DistBizLineCodeEnum.DATING.getCode();

    @Resource
    private DistRefBindMapper distRefBindMapper;
    @Resource
    private DistRuleMapper distRuleMapper;
    @Resource
    private DistAccrualMapper distAccrualMapper;
    @Resource
    private DistServicePeriodMapper distServicePeriodMapper;
    @Resource
    private BizWalAccountInternalService bizWalAccountInternalService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onOrderGoodsPaid(DistOrderPaidNotifyDTO dto) {
        if (dto == null || StrUtil.isBlank(dto.getTdOdSysUserCode()) || StrUtil.isBlank(dto.getTdOdGdCode())) {
            return;
        }
        String payerUserCode = dto.getTdOdSysUserCode();
        BigDecimal baseAmount = dto.getTdOdGdAmount();
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        String bizLine = DEFAULT_BIZ_LINE;
        DistRefBind bind = findBind(bizLine, payerUserCode);
        if (bind == null) {
            return;
        }
        String directInviter = bind.getDistInviterUserCode();
        if (StrUtil.isBlank(directInviter) || Objects.equals(directInviter, payerUserCode)) {
            return;
        }
        String indirectInviter = resolveIndirectInviter(directInviter);

        accrueForBeneficiary(dto, bizLine, payerUserCode, directInviter, "1", baseAmount);
        if (StrUtil.isNotBlank(indirectInviter) && !Objects.equals(indirectInviter, payerUserCode)) {
            accrueForBeneficiary(dto, bizLine, payerUserCode, indirectInviter, "2", baseAmount);
        }
    }

    private void accrueForBeneficiary(DistOrderPaidNotifyDTO dto, String bizLine, String payerUserCode,
            String beneficiaryUserCode, String levelCode, BigDecimal baseAmount) {
        String sourceId = dto.getTdOdGdCode();
        long exists = distAccrualMapper.selectCount(new QueryWrapper<DistAccrual>().lambda()
                .eq(DistAccrual::getDistAccrualSourceTypeCode, DistAccrualSourceTypeCodeEnum.ORDER_PAID.getCode())
                .eq(DistAccrual::getDistAccrualSourceId, sourceId)
                .eq(DistAccrual::getDistBeneficiaryUserCode, beneficiaryUserCode)
                .eq(DistAccrual::getDistRuleLevelCode, levelCode));
        if (exists > 0) {
            return;
        }

        DistRule rule = matchRule(bizLine, levelCode, beneficiaryUserCode, dto.getTdGdCgyCode());
        if (rule == null) {
            log.debug("no dist rule matched bizLine={} level={} beneficiary={}", bizLine, levelCode, beneficiaryUserCode);
            return;
        }

        BigDecimal commission = baseAmount.multiply(rule.getDistRuleRate()).setScale(2, RoundingMode.HALF_UP);
        if (commission.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        boolean onServiceEnd = DistSettleModeCodeEnum.ON_SERVICE_END.getCode().equals(rule.getDistSettleModeCode())
                || DistPeriodParseUtil.hasServicePeriod(dto.getTdGdPeriod());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime settleAt = onServiceEnd
                ? DistPeriodParseUtil.resolveEndAt(now, dto.getTdGdPeriod())
                : now;

        DistAccrual accrual = new DistAccrual();
        accrual.setId(IdUtil.getSnowflakeNextIdStr());
        accrual.setDistAccrualCode(IdUtil.getSnowflakeNextIdStr());
        accrual.setDistBizLineCode(bizLine);
        accrual.setDistAccrualSourceTypeCode(DistAccrualSourceTypeCodeEnum.ORDER_PAID.getCode());
        accrual.setDistAccrualSourceId(sourceId);
        accrual.setDistPayerUserCode(payerUserCode);
        accrual.setDistBeneficiaryUserCode(beneficiaryUserCode);
        accrual.setDistRuleLevelCode(levelCode);
        accrual.setDistBaseAmount(baseAmount);
        accrual.setDistRuleRate(rule.getDistRuleRate());
        accrual.setDistCommissionAmount(commission);
        accrual.setTdOdCode(dto.getTdOdCode());
        accrual.setTdGdCgyCode(dto.getTdGdCgyCode());
        accrual.setDistSettleAt(settleAt);

        if (onServiceEnd && settleAt.isAfter(now)) {
            accrual.setDistAccrualStatusCode(DistAccrualStatusCodeEnum.PENDING.getCode());
            distAccrualMapper.insert(accrual);
            saveServicePeriod(sourceId, now, settleAt);
        } else {
            accrual.setDistAccrualStatusCode(DistAccrualStatusCodeEnum.SETTLED.getCode());
            distAccrualMapper.insert(accrual);
            WalAccount account = bizWalAccountInternalService.getOrCreate(bizLine, beneficiaryUserCode);
            bizWalAccountInternalService.creditSettledCommission(account, commission, accrual.getDistAccrualCode());
        }
    }

    private DistRule matchRule(String bizLine, String levelCode, String beneficiaryUserCode, String tdGdCgyCode) {
        String roleCode = resolvePromoterRole(bizLine, beneficiaryUserCode);
        List<DistRule> rules = distRuleMapper.selectList(new QueryWrapper<DistRule>().lambda()
                .eq(DistRule::getDistBizLineCode, bizLine)
                .eq(DistRule::getDistRuleLevelCode, levelCode)
                .eq(DistRule::getDistRuleEnabledCode, DistRuleEnabledCodeEnum.ENABLED.getCode()));
        for (DistRule rule : rules) {
            if (!matchesRole(rule.getDistPromoterRoleCode(), roleCode)) {
                continue;
            }
            if (!matchesProductScope(rule.getDistProductScopeJson(), tdGdCgyCode)) {
                continue;
            }
            return rule;
        }
        return null;
    }

    private boolean matchesRole(String ruleRole, String actualRole) {
        if (DistPromoterRoleCodeEnum.ANY.getCode().equals(ruleRole)) {
            return true;
        }
        return Objects.equals(ruleRole, actualRole);
    }

    private boolean matchesProductScope(String scopeJson, String tdGdCgyCode) {
        if (StrUtil.isBlank(scopeJson)) {
            return true;
        }
        try {
            List<String> scopes = JSONUtil.toList(scopeJson, String.class);
            return scopes.isEmpty() || scopes.contains(tdGdCgyCode);
        } catch (Exception ex) {
            return true;
        }
    }

    private String resolvePromoterRole(String bizLine, String beneficiaryUserCode) {
        Map<String, SpiDistPromoterRoleResolver> resolvers;
        try {
            resolvers = SpringUtil.getBeansOfType(SpiDistPromoterRoleResolver.class);
        } catch (Exception ex) {
            resolvers = null;
        }
        if (resolvers != null) {
            for (SpiDistPromoterRoleResolver resolver : resolvers.values()) {
                if (resolver.supports(bizLine)) {
                    return resolver.resolvePromoterRoleCode(beneficiaryUserCode);
                }
            }
        }
        return DistPromoterRoleCodeEnum.NORMAL.getCode();
    }

    private DistRefBind findBind(String bizLine, String inviteeUserCode) {
        DistRefBind bind = distRefBindMapper.selectOne(new QueryWrapper<DistRefBind>().lambda()
                .eq(DistRefBind::getDistBizLineCode, bizLine)
                .eq(DistRefBind::getDistInviteeUserCode, inviteeUserCode)
                .eq(DistRefBind::getDistRefBindStatusCode, DistRefBindStatusCodeEnum.VALID.getCode()), false);
        if (bind != null) {
            return bind;
        }
        UserDTO user = apiSysUserService.getUserByUserCode(inviteeUserCode);
        if (user == null || StrUtil.isBlank(user.getUserReferenceUserCode())) {
            return null;
        }
        bind = new DistRefBind();
        bind.setId(IdUtil.getSnowflakeNextIdStr());
        bind.setDistRefBindCode(IdUtil.getSnowflakeNextIdStr());
        bind.setDistBizLineCode(bizLine);
        bind.setDistInviteeUserCode(inviteeUserCode);
        bind.setDistInviterUserCode(user.getUserReferenceUserCode());
        bind.setDistRefBindSourceCode(DistRefBindSourceCodeEnum.SYNC.getCode());
        bind.setDistRefBindStatusCode(DistRefBindStatusCodeEnum.VALID.getCode());
        bind.setDistBindTime(user.getCreateTime() != null ? user.getCreateTime() : LocalDateTime.now());
        distRefBindMapper.insert(bind);
        return bind;
    }

    private String resolveIndirectInviter(String directInviterUserCode) {
        UserDTO inviter = apiSysUserService.getUserByUserCode(directInviterUserCode);
        if (inviter == null) {
            return null;
        }
        return StrUtil.trim(inviter.getUserReferenceUserCode());
    }

    private void saveServicePeriod(String sourceId, LocalDateTime startAt, LocalDateTime endAt) {
        DistServicePeriod existing = distServicePeriodMapper.selectOne(new QueryWrapper<DistServicePeriod>().lambda()
                .eq(DistServicePeriod::getDistAccrualSourceId, sourceId), false);
        if (existing != null) {
            return;
        }
        DistServicePeriod period = new DistServicePeriod();
        period.setId(IdUtil.getSnowflakeNextIdStr());
        period.setDistServicePeriodCode(IdUtil.getSnowflakeNextIdStr());
        period.setDistAccrualSourceId(sourceId);
        period.setDistPeriodStartAt(startAt);
        period.setDistPeriodEndAt(endAt);
        period.setDistPeriodStatusCode(DistPeriodStatusCodeEnum.ACTIVE.getCode());
        distServicePeriodMapper.insert(period);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int settleDueServicePeriods() {
        LocalDateTime now = LocalDateTime.now();
        List<DistServicePeriod> dueList = distServicePeriodMapper.selectList(new QueryWrapper<DistServicePeriod>().lambda()
                .eq(DistServicePeriod::getDistPeriodStatusCode, DistPeriodStatusCodeEnum.ACTIVE.getCode())
                .le(DistServicePeriod::getDistPeriodEndAt, now));
        int count = 0;
        for (DistServicePeriod period : dueList) {
            List<DistAccrual> accruals = distAccrualMapper.selectList(new QueryWrapper<DistAccrual>().lambda()
                    .eq(DistAccrual::getDistAccrualSourceId, period.getDistAccrualSourceId())
                    .eq(DistAccrual::getDistAccrualStatusCode, DistAccrualStatusCodeEnum.PENDING.getCode()));
            for (DistAccrual accrual : accruals) {
                accrual.setDistAccrualStatusCode(DistAccrualStatusCodeEnum.SETTLED.getCode());
                distAccrualMapper.updateById(accrual);
                WalAccount account = bizWalAccountInternalService.getOrCreate(accrual.getDistBizLineCode(),
                        accrual.getDistBeneficiaryUserCode());
                bizWalAccountInternalService.creditSettledCommission(account, accrual.getDistCommissionAmount(),
                        accrual.getDistAccrualCode());
                count++;
            }
            period.setDistPeriodStatusCode(DistPeriodStatusCodeEnum.ENDED.getCode());
            distServicePeriodMapper.updateById(period);
        }
        return count;
    }
}
