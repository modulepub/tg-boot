package pub.module.dating.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import pub.module.common.enums.BaseEntityFiled;
import pub.module.dating.api.constants.RecommendedSourceCodeEnum;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.crud.entity.DtRecommended;
import pub.module.dating.crud.service.DtRecommendedService;
import pub.module.dating.api.service.*;
import pub.module.dating.biz.service.DatingWxSubscribeNotifyService;
import pub.module.dating.biz.util.FreeRecommendMatchScoreUtil;
import pub.module.system.api.constants.SysUserBadgeKeyEnum;
import pub.module.system.api.service.ApiSysUserBadgeService;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Api 对象推荐 Service
 *
 * @author tg
 * 2026-03-30 00:52:26
 */
@Service
public class ApiDtRecommendedServiceImpl implements ApiDtRecommendedService {

    @Resource
    DtRecommendedService dtRecommendedService;
    @Resource
    ApiDtCustomerService apiDtCustomerService;
    @Resource
    DatingWxSubscribeNotifyService datingWxSubscribeNotifyService;
    @Resource
    ApiDtIntentionService apiDtIntentionService;
    @Resource
    ApiSysUserBadgeService apiSysUserBadgeService;

    @Override
    public void synFreeRecommend(DtIntentionDTO dtIntentionDTO, String userCode, int recommendCount) {
        synFreeRecommend(dtIntentionDTO, userCode, recommendCount, null);
    }

    @Override
    public void synFreeRecommend(DtIntentionDTO dtIntentionDTO, String userCode, int recommendCount, String leadCusCode) {
        if (dtIntentionDTO == null || StrUtil.isBlank(userCode) || recommendCount <= 0) {
            return;
        }
        DtCustomerDTO selfCustomer = apiDtCustomerService.getCusByUserCode(userCode);
        if (selfCustomer != null && StatusCodeEnum.YES.equals(selfCustomer.getCusTestStatusCode())) {
            return;
        }
        long todayCount = countTodayFreeRecommended(userCode);
        if (todayCount >= recommendCount) {
            return;
        }
        int needCount = (int) Math.min(recommendCount - todayCount, Integer.MAX_VALUE);
        if (needCount <= 0) {
            return;
        }

        List<String> excludeCusCodes = new ArrayList<>(dtRecommendedService.list(
                new QueryWrapper<DtRecommended>().lambda()
                        .select(DtRecommended::getCusCode)
                        .eq(DtRecommended::getUserCode, userCode))
                .stream()
                .map(DtRecommended::getCusCode)
                .filter(StrUtil::isNotBlank)
                .toList());
        if (selfCustomer != null && StrUtil.isNotBlank(selfCustomer.getCusCode())) {
            excludeCusCodes.add(selfCustomer.getCusCode().trim());
        }

        DtCustomerDTO customerQuery = new DtCustomerDTO();
        customerQuery.setCusSexCode(dtIntentionDTO.getIntentionSexCode());
        List<DtCustomerDTO> candidates = apiDtCustomerService.findCustomer(excludeCusCodes, customerQuery);

        List<DtCustomerDTO> selected = new ArrayList<>();
        String lead = StrUtil.trim(leadCusCode);
        if (StrUtil.isNotBlank(lead) && needCount > 0 && !excludeCusCodes.contains(lead)) {
            DtCustomerDTO leadCustomer = apiDtCustomerService.getCusByCusCode(lead);
            if (leadCustomer != null) {
                selected.add(leadCustomer);
                excludeCusCodes.add(lead);
                needCount--;
            }
        }
        if (needCount > 0 && candidates != null && !candidates.isEmpty()) {
            List<DtCustomerDTO> more = candidates.stream()
                    .filter(c -> c != null && StrUtil.isNotBlank(c.getCusCode()))
                    .filter(c -> !excludeCusCodes.contains(c.getCusCode().trim()))
                    .sorted(Comparator
                            .comparing((DtCustomerDTO c) -> FreeRecommendMatchScoreUtil.calc(dtIntentionDTO, c))
                            .reversed())
                    .limit(needCount)
                    .toList();
            selected.addAll(more);
        }
        if (selected.isEmpty()) {
            return;
        }

        List<DtCustomerDTO> selectedFinal = selected;
        List<DtRecommended> dtRecommendedList = new ArrayList<>();
        for (DtCustomerDTO item : selectedFinal) {
            DtRecommended dtRecommended = BeanUtil.copyProperties(item, DtRecommended.class, BaseEntityFiled.NAMES);
            fillCustomerSnapshot(dtRecommended, item);
            dtRecommended.setRecommendedSourceCode(RecommendedSourceCodeEnum.FREE);
            dtRecommended.setRecommendedMatchScore(FreeRecommendMatchScoreUtil.calc(dtIntentionDTO, item));
            dtRecommended.setUserCode(userCode);
            fillSelfIntentionSnapshot(dtRecommended, dtIntentionDTO);
            dtRecommendedList.add(dtRecommended);
        }
        if (!dtRecommendedList.isEmpty()) {
            dtRecommendedService.saveBatch(dtRecommendedList);
            datingWxSubscribeNotifyService.sendFreeRecommendNotify(userCode, selectedFinal);
            apiSysUserBadgeService.incrementBadgeCount(
                    userCode,
                    SysUserBadgeKeyEnum.ME_RECOMMEND.getCode(),
                    dtRecommendedList.size());
        }
    }

    /** 今日已产生的免费推荐条数 */
    private long countTodayFreeRecommended(String userCode) {
        LocalDateTime dayStart = LocalDate.now().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        return dtRecommendedService.lambdaQuery()
                .eq(DtRecommended::getUserCode, userCode.trim())
                .eq(DtRecommended::getRecommendedSourceCode, RecommendedSourceCodeEnum.FREE)
                .ge(DtRecommended::getCreateTime, dayStart)
                .lt(DtRecommended::getCreateTime, dayEnd)
                .count();
    }

    /** 列表返回前，用客户表最新快照回填推荐行冗余字段（昵称、实名、爱与诚等） */
    public void enrichRecommendPageFromCustomer(IPage<DtRecommended> page) {
        enrichRecommendPageFromCustomer(page, null);
    }

    /**
     * 列表返回前回填客户快照；{@code viewerUserCode} 非空时额外补全「我的意向」快照与对方意向联查字段（推荐历史列表）。
     */
    public void enrichRecommendPageFromCustomer(IPage<DtRecommended> page, String viewerUserCode) {
        if (page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
            return;
        }
        List<String> cusCodes = page.getRecords().stream()
                .map(DtRecommended::getCusCode)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
        if (cusCodes.isEmpty()) {
            return;
        }
        List<DtCustomerDTO> customers = apiDtCustomerService.listByCusCodes(cusCodes);
        if (customers == null || customers.isEmpty()) {
            return;
        }
        Map<String, DtCustomerDTO> byCode = customers.stream()
                .filter(c -> StrUtil.isNotBlank(c.getCusCode()))
                .collect(Collectors.toMap(c -> c.getCusCode().trim(), c -> c, (a, b) -> a));
        for (DtRecommended row : page.getRecords()) {
            String code = StrUtil.trim(row.getCusCode());
            if (StrUtil.isBlank(code)) {
                continue;
            }
            DtCustomerDTO cus = byCode.get(code);
            if (cus != null) {
                fillCustomerSnapshot(row, cus);
            }
        }
        if (StrUtil.isNotBlank(viewerUserCode)) {
            enrichSelfIntentionSnapshotIfAbsent(page, viewerUserCode.trim());
            enrichTargetIntentionFromCustomers(page, byCode);
        }
    }

    /** 推荐行未落库「我的意向」时，用当前登录用户最新意向回填展示字段 */
    private void enrichSelfIntentionSnapshotIfAbsent(IPage<DtRecommended> page, String viewerUserCode) {
        DtIntentionDTO self = apiDtIntentionService.findDtIntentionIfPresent(viewerUserCode);
        if (self == null) {
            return;
        }
        for (DtRecommended row : page.getRecords()) {
            if (!hasSelfIntentionSnapshot(row)) {
                fillSelfIntentionSnapshot(row, self);
            }
        }
    }

    /** 按被推荐嘉宾 userCode 联查 dt_intention，写入对方意向展示字段 */
    private void enrichTargetIntentionFromCustomers(IPage<DtRecommended> page, Map<String, DtCustomerDTO> customerByCusCode) {
        List<String> targetUserCodes = customerByCusCode.values().stream()
                .map(DtCustomerDTO::getCusUserCode)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
        if (targetUserCodes.isEmpty()) {
            return;
        }
        Map<String, DtIntentionDTO> intentionByUser = apiDtIntentionService.findDtIntentionByUserCodes(targetUserCodes);
        if (intentionByUser.isEmpty()) {
            return;
        }
        for (DtRecommended row : page.getRecords()) {
            String cusCode = StrUtil.trim(row.getCusCode());
            if (StrUtil.isBlank(cusCode)) {
                continue;
            }
            DtCustomerDTO cus = customerByCusCode.get(cusCode);
            if (cus == null || StrUtil.isBlank(cus.getCusUserCode())) {
                continue;
            }
            DtIntentionDTO target = intentionByUser.get(cus.getCusUserCode().trim());
            if (target != null) {
                fillTargetIntentionSnapshot(row, target);
            }
        }
    }

    private static boolean hasSelfIntentionSnapshot(DtRecommended row) {
        if (row == null) {
            return false;
        }
        return row.getIntentionMinAge() != null
                || row.getIntentionMaxAge() != null
                || StrUtil.isNotBlank(row.getIntentionCityCode())
                || row.getIntentionHaveHouseCode() != null
                || row.getIntentionHaveCarCode() != null
                || row.getIntentionDisabledStatusCode() != null
                || row.getIntentionSexCode() != null;
    }

    /** 写入推荐表「我的意向」冗余字段（推荐产生时快照） */
    public static void fillSelfIntentionSnapshot(DtRecommended row, DtIntentionDTO intention) {
        if (row == null || intention == null) {
            return;
        }
        row.setIntentionMinAge(intention.getIntentionMinAge());
        row.setIntentionMaxAge(intention.getIntentionMaxAge());
        row.setIntentionCityCode(intention.getIntentionCityCode());
        row.setIntentionHaveHouseCode(intention.getIntentionHaveHouseCode());
        row.setIntentionHaveCarCode(intention.getIntentionHaveCarCode());
        row.setIntentionSexCode(intention.getIntentionSexCode());
        row.setIntentionDisabledStatusCode(intention.getIntentionDisabledStatusCode());
    }

    /** 写入列表联查的对方意向展示字段 */
    public static void fillTargetIntentionSnapshot(DtRecommended row, DtIntentionDTO intention) {
        if (row == null || intention == null) {
            return;
        }
        row.setRecommendedTargetIntentionMinAge(intention.getIntentionMinAge());
        row.setRecommendedTargetIntentionMaxAge(intention.getIntentionMaxAge());
        row.setRecommendedTargetIntentionCityCode(intention.getIntentionCityCode());
        row.setRecommendedTargetIntentionHaveHouseCode(intention.getIntentionHaveHouseCode());
        row.setRecommendedTargetIntentionHaveCarCode(intention.getIntentionHaveCarCode());
        row.setRecommendedTargetIntentionDisabledStatusCode(intention.getIntentionDisabledStatusCode());
    }

    /** 写入推荐表冗余展示字段（昵称、实名、爱与诚、是否隐藏等） */
    public static void fillCustomerSnapshot(DtRecommended row, DtCustomerDTO cus) {
        if (row == null || cus == null) {
            return;
        }
        row.setCusNickName(StrUtil.trimToNull(cus.getCusNickName()));
        row.setCusIdentityAuthenticatedStatusCode(cus.getCusIdentityAuthenticatedStatusCode());
        row.setCusLsStatusCode(cus.getCusLsStatusCode());
        row.setCusHiddenStatusCode(cus.getCusHiddenStatusCode());
    }

    /** 排除已隐藏主页的推荐行（冗余字段 cus_hidden_status_code） */
    public static void excludeHiddenRecommended(QueryWrapper<DtRecommended> queryWrapper) {
        queryWrapper.lambda().and(w -> w
                .isNull(DtRecommended::getCusHiddenStatusCode)
                .or()
                .ne(DtRecommended::getCusHiddenStatusCode, StatusCodeEnum.YES));
    }

    /** 排除用户已从推荐历史列表删除的记录（recommended_cus_del_status_code） */
    public static void excludeCustomerDeletedRecommended(QueryWrapper<DtRecommended> queryWrapper) {
        queryWrapper.lambda().and(w -> w
                .isNull(DtRecommended::getRecommendedCusDelStatusCode)
                .or()
                .ne(DtRecommended::getRecommendedCusDelStatusCode, StatusCodeEnum.YES));
    }

    /** 首页推荐列表：将访客预览嘉宾置顶为第一条 */
    public void promoteLeadCusOnFirstPage(IPage<DtRecommended> page, String leadCusCode, String userCode) {
        if (page == null || page.getRecords() == null || page.getRecords().isEmpty()
                || StrUtil.isBlank(leadCusCode) || StrUtil.isBlank(userCode)) {
            return;
        }
        String lead = leadCusCode.trim();
        List<DtRecommended> records = new ArrayList<>(page.getRecords());
        int hit = -1;
        for (int i = 0; i < records.size(); i++) {
            if (lead.equals(StrUtil.trim(records.get(i).getCusCode()))) {
                hit = i;
                break;
            }
        }
        if (hit > 0) {
            DtRecommended row = records.remove(hit);
            records.add(0, row);
            page.setRecords(records);
            return;
        }
        if (hit == 0) {
            return;
        }
        DtRecommended leadRow = dtRecommendedService.lambdaQuery()
                .eq(DtRecommended::getUserCode, userCode.trim())
                .eq(DtRecommended::getCusCode, lead)
                .orderByDesc(DtRecommended::getCreateTime)
                .last("LIMIT 1")
                .one();
        if (leadRow == null) {
            return;
        }
        DtCustomerDTO cus = apiDtCustomerService.getCusByCusCode(lead);
        if (cus != null) {
            fillCustomerSnapshot(leadRow, cus);
        }
        records.add(0, leadRow);
        long pageSize = page.getSize();
        if (pageSize > 0 && records.size() > pageSize) {
            records = new ArrayList<>(records.subList(0, (int) pageSize));
        }
        page.setRecords(records);
    }
}
