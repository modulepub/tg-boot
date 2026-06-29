package pub.module.dating.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.constants.CusMemberBenefitErrorCodeEnum;
import pub.module.dating.api.constants.CusMemberBenefitTypeEnum;
import pub.module.dating.api.constants.CusSourceCodeEnum;
import pub.module.dating.api.constants.CusMemberTierConstants;
import pub.module.trade.api.dto.TdGoodsMemberBenefitDeltaDTO;
import pub.module.trade.api.service.ApiTdGoodsService;
import pub.module.dating.api.messaging.DtProfileUpdatedMessage;
import pub.module.dating.biz.messaging.DtProfileUpdatedPublisher;
import pub.module.dating.biz.support.DtCustomerContentModerationSupport;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtGuestPreviewRecommendDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.api.service.dto.MemberBenefitConsumeResultDTO;
import pub.module.dating.biz.util.CustomerProfileCompletenessRateUtil;
import pub.module.dating.biz.util.FreeRecommendMatchScoreUtil;
import pub.module.dating.crud.entity.DtCustomer;
import pub.module.dating.crud.entity.DtMemberBenefitConsumeRecord;
import pub.module.dating.crud.entity.DtMemberBenefitRechargeRecord;
import pub.module.dating.crud.service.DtMemberBenefitConsumeRecordService;
import pub.module.dating.crud.service.DtMemberBenefitRechargeRecordService;
import pub.module.dating.crud.service.DtCustomerService;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.constants.UserSexCodeEnum;
import pub.module.system.api.service.dto.UserDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Api 客户 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Slf4j
@Service
public class ApiDtCustomerServiceImpl implements ApiDtCustomerService {
    @Resource
    DtCustomerService customerService;
    @Resource
    ApiSysUserService apiSysUserService;
    @Resource
    ApiImService apiImService;
    @Resource
    DtMemberBenefitRechargeRecordService customerMemberBenefitRechargeRecordService;
    @Resource
    DtMemberBenefitConsumeRecordService customerMemberBenefitConsumeRecordService;
    @Resource
    DtProfileUpdatedPublisher datingProfileUpdatedPublisher;
    @Resource
    ApiTdGoodsService apiTdGoodsService;
    @Resource
    DtCustomerContentModerationSupport dtCustomerContentModerationSupport;
    /**
     * 用户端 patch 合并到实体时跳过的属性（权益、审计、绑定关系、仅校验字段等）。
     */
    private static final String[] CUS_PATCH_IGNORE_PROPERTY_NAMES = {
            "id", "createBy", "createTime", "updateBy", "updateTime",
            "deleted", "version", "seqNo", "orgCode",
            "cusUserCode", "cusCode",
            "cusAddFriendRightValue", "cusRecommendRightValue", "cusMatchRightValue",
            "cusAddFriendDayLimit", "cusRecommendDayLimit", "cusMatchDayLimit",
            "cusMemberTypeCode", "cusMemberTypeName", "cusMemberExpireTime",
            "cusHandholdsNum", "cusAssignSalesTimeRangeArray",
            "cusAuditProcessCode",
    };

    private static final CopyOptions CUSTOMER_PATCH_COPY_OPTIONS = CopyOptions.create()
            .setIgnoreNullValue(true)
            .setIgnoreProperties(CUS_PATCH_IGNORE_PROPERTY_NAMES);

    @Override
    public void importData(Map<String, Object> data) {
        DtCustomer customer = BeanUtil.copyProperties(data, DtCustomer.class);
        customer.setCusSourceCode(CusSourceCodeEnum.IMPORT);
        log.info("导入客户数据数据{}", customer);
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusPhone, customer.getCusPhone()).or().eq(DtCustomer::getCusIdNo, customer.getCusIdNo());
        DtCustomer old = customerService.getOne(queryWrapper, false);
        if (old != null) {
            BeanUtil.copyProperties(old, customer, CopyOptions.create().setIgnoreNullValue(true));
            customerService.updateById(customer);
        } else {
            customerService.save(customer);
        }
        syncUserRealNameFromCustomer(customer.getCusUserCode(), customer.getCusName());
        if (StrUtil.isNotBlank(customer.getCusUserCode())) {
            publishCustomerProfileUpdated(customer.getCusUserCode().trim(), customer);
        }
    }

    @Override
    public void initCustomerByUser(UserDTO user) {
        Assert.notNull(user, "user 不能为空");
        String userCode = StrUtil.trim(user.getUserCode());
        Assert.notBlank(userCode, "user.userCode 不能为空");
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode);
        if (customerService.count(queryWrapper) > 0) {
            return;
        }
        DtCustomer customer = new DtCustomer();
        customer.setCusUserCode(userCode);
        customer.setCusIdentityAuthenticatedStatusCode(StatusCodeEnum.NO);
        customer.setCusHiddenStatusCode(StatusCodeEnum.NO);
        customerService.save(customer);
    }

    @Override
    public void syncCusNickNameFromUser(UserDTO user) {
        Assert.notNull(user, "user 不能为空");
        String userCode = StrUtil.trim(user.getUserCode());
        Assert.notBlank(userCode, "user.userCode 不能为空");
        String nickName = StrUtil.trim(user.getUserNickName());
        if (StrUtil.isBlank(nickName)) {
            return;
        }
        initCustomerByUser(new UserDTO().setUserCode(userCode));
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode);
        DtCustomer customer = customerService.getOne(queryWrapper, false);
        if (customer == null) {
            return;
        }
        if (nickName.equals(StrUtil.trim(customer.getCusNickName()))) {
            return;
        }
        LambdaUpdateWrapper<DtCustomer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DtCustomer::getCusUserCode, userCode).set(DtCustomer::getCusNickName, nickName);
        customerService.update(updateWrapper);
        customer.setCusNickName(nickName);
        publishCustomerProfileUpdated(userCode, customer);
        log.info("已同步客户昵称 userCode={} cusNickName={}", userCode, nickName);
    }

    @Override
    public void syncUserRealNameFromCustomer(String userCode, String cusName) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(cusName)) {
            return;
        }
        String normalizedCode = userCode.trim();
        String normalizedName = cusName.trim();
        UserDTO user = apiSysUserService.getUserByUserCode(normalizedCode);
        if (user == null) {
            return;
        }
        if (normalizedName.equals(StrUtil.trim(user.getUserRealName()))) {
            return;
        }
        apiSysUserService.updateUserRealNameByUserCode(normalizedCode, normalizedName);
        log.info("已同步用户真实姓名 userCode={} userRealName={}", normalizedCode, normalizedName);
    }

    @Override
    public void syncUserNickNameFromCustomer(String userCode, String cusNickName) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(cusNickName)) {
            return;
        }
        String normalizedCode = userCode.trim();
        String normalizedNick = cusNickName.trim();
        UserDTO user = apiSysUserService.getUserByUserCode(normalizedCode);
        if (user == null) {
            return;
        }
        if (normalizedNick.equals(StrUtil.trim(user.getUserNickName()))) {
            return;
        }
        apiSysUserService.updateUserNickNameByUserCode(normalizedCode, normalizedNick);
        log.info("已同步用户昵称 userCode={} userNickName={}", normalizedCode, normalizedNick);
    }

    @Override
    public void syncUserIdentityAuthenticatedFromCustomer(String userCode, StatusCodeEnum status) {
        if (StrUtil.isBlank(userCode) || status == null) {
            return;
        }
        String normalizedCode = userCode.trim();
        UserDTO user = apiSysUserService.getUserByUserCode(normalizedCode);
        if (user == null) {
            return;
        }
        if (status.equals(user.getUserIdentityAuthenticatedStatusCode())) {
            return;
        }
        apiSysUserService.updateUserIdentityAuthenticatedStatusByUserCode(normalizedCode, status);
        log.info("已同步用户实名认证状态 userCode={} userIdentityAuthenticatedStatusCode={}",
                normalizedCode, status.getCode());
    }

    @Override
    public DtCustomerDTO getCusByUserCode(String userCode) {
        Assert.notBlank(userCode, "userCode 不能为空");
        initCustomerByUser(new UserDTO().setUserCode(userCode));
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode);
        DtCustomer customer = customerService.getOne(queryWrapper, false);
        return BeanUtil.copyProperties(customer, DtCustomerDTO.class);
    }

    @Override
    public List<DtCustomerDTO> listAll(List<String> notIn) {
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().notIn(DtCustomer::getCusCode, notIn);
        List<DtCustomer> customerList = customerService.list(queryWrapper);
        return BeanUtil.copyToList(customerList, DtCustomerDTO.class);
    }

    @Override
    public DtCustomerDTO getCusByCusCode(String cusCode) {
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusCode, cusCode);
        DtCustomer customer = customerService.getOne(queryWrapper, false);
        return BeanUtil.copyProperties(customer,DtCustomerDTO.class);
    }

    @Override
    public List<DtCustomerDTO> findCustomer(List<String> notIn, DtCustomerDTO customerDto) {
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        if(notIn!=null&&!notIn.isEmpty()){
            queryWrapper.lambda().notIn(DtCustomer::getCusCode, notIn);
        }
        if(customerDto.getCusSexCode()!=null){
            queryWrapper.lambda().eq(DtCustomer::getCusSexCode,customerDto.getCusSexCode().getCode());
        }
        excludeHiddenCustomers(queryWrapper);
        excludeIncompleteProfileCustomers(queryWrapper);
        excludeUnauthenticatedCustomers(queryWrapper);
        List<DtCustomer> customerList = customerService.list(queryWrapper);
        return BeanUtil.copyToList(customerList,DtCustomerDTO.class);
    }

    @Override
    public List<DtCustomerDTO> listGuestPreviewBySexCode(String intentionSexCode, int limit) {
        if (StrUtil.isBlank(intentionSexCode) || limit <= 0) {
            return Collections.emptyList();
        }
        UserSexCodeEnum sex = UserSexCodeEnum.fromJson(intentionSexCode.trim());
        if (sex == null) {
            return Collections.emptyList();
        }
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusSexCode, sex.getCode());
        excludeHiddenCustomers(queryWrapper);
        excludeIncompleteProfileCustomers(queryWrapper);
        excludeUnauthenticatedCustomers(queryWrapper);
        queryWrapper.lambda().orderByDesc(DtCustomer::getCreateTime).last("LIMIT " + limit);
        List<DtCustomer> customerList = customerService.list(queryWrapper);
        if (customerList == null || customerList.isEmpty()) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(customerList, DtCustomerDTO.class);
    }

    @Override
    public List<DtGuestPreviewRecommendDTO> listGuestPreviewByIntention(DtIntentionDTO intention, int limit) {
        if (intention == null || intention.getIntentionSexCode() == null || limit <= 0) {
            return Collections.emptyList();
        }
        DtCustomerDTO customerQuery = new DtCustomerDTO();
        customerQuery.setCusSexCode(intention.getIntentionSexCode());
        List<DtCustomerDTO> candidates = findCustomer(null, customerQuery);
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        return candidates.stream()
                .filter(c -> StatusCodeEnum.YES.equals(c.getCusIdentityAuthenticatedStatusCode()))
                .sorted(Comparator
                        .comparing((DtCustomerDTO c) -> FreeRecommendMatchScoreUtil.calc(intention, c))
                        .reversed())
                .limit(limit)
                .map(c -> new DtGuestPreviewRecommendDTO()
                        .setCustomer(c)
                        .setRecommendedMatchScore(FreeRecommendMatchScoreUtil.calc(intention, c)))
                .toList();
    }

    /** 排除资料未完善的客户（null 视同未完善） */
    private static void excludeIncompleteProfileCustomers(QueryWrapper<DtCustomer> queryWrapper) {
        queryWrapper.lambda().eq(DtCustomer::getCusComleteProfileStatusCode, StatusCodeEnum.YES);
    }

    /** 排除未实名认证的客户（null 视同未实名） */
    private static void excludeUnauthenticatedCustomers(QueryWrapper<DtCustomer> queryWrapper) {
        queryWrapper.lambda().eq(DtCustomer::getCusIdentityAuthenticatedStatusCode, StatusCodeEnum.YES);
    }

    /** 排除已隐藏主页的客户（null 视同未隐藏） */
    private static void excludeHiddenCustomers(QueryWrapper<DtCustomer> queryWrapper) {
        queryWrapper.lambda().and(w -> w
                .isNull(DtCustomer::getCusHiddenStatusCode)
                .or()
                .ne(DtCustomer::getCusHiddenStatusCode, StatusCodeEnum.YES));
    }

    @Override
    public List<DtCustomerDTO> listCusByNickNameExact(String cusNickName) {
        String nick = StrUtil.trim(cusNickName);
        if (StrUtil.isBlank(nick)) {
            return Collections.emptyList();
        }
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusNickName, nick);
        excludeHiddenCustomers(queryWrapper);
        List<DtCustomer> customers = customerService.list(queryWrapper);
        if (customers == null || customers.isEmpty()) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(customers, DtCustomerDTO.class);
    }

    @Override
    public DtCustomerDTO getCusByNickNameExact(String cusNickName) {
        List<DtCustomerDTO> list = listCusByNickNameExact(cusNickName);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<DtCustomerDTO> listByCusCodes(Collection<String> cusCodes) {
        if (cusCodes == null || cusCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> codes = cusCodes.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(DtCustomer::getCusCode, codes);
        List<DtCustomer> list = customerService.list(queryWrapper);
        return BeanUtil.copyToList(list, DtCustomerDTO.class);
    }

    @Override
    public List<DtCustomerDTO> listByUserCodes(Collection<String> userCodes) {
        if (userCodes == null || userCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> codes = userCodes.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(DtCustomer::getCusUserCode, codes);
        List<DtCustomer> list = customerService.list(queryWrapper);
        return BeanUtil.copyToList(list, DtCustomerDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtCustomerDTO updateCurrCustomerPartial(String userCode, DtCustomerDTO patchDto) {
        return persistCustomerPatch(userCode, patchDto, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtCustomerDTO updateMockCustomerPartial(String userCode, DtCustomerDTO patchDto) {
        return persistCustomerPatch(userCode, patchDto, false);
    }

    private DtCustomerDTO persistCustomerPatch(String userCode, DtCustomerDTO patchDto, boolean publishProfileEvent) {
        Assert.notBlank(userCode, "用户未登录");
        if (patchDto == null) {
            patchDto = new DtCustomerDTO();
        }

        // --- 持久化入库 ---
        DtCustomer entity = loadOrCreateCustomer(userCode);
        validatePatchCusCode(patchDto, entity);
        normalizeCustomerPatch(patchDto);
        if (publishProfileEvent) {
            dtCustomerContentModerationSupport.moderatePatch(userCode, entity.getCusCode(), entity.getCusName(), patchDto);
        }
        DtCustomer beforePatch = BeanUtil.copyProperties(entity, DtCustomer.class);
        applyCustomerPatchToEntity(patchDto, entity);
        stampAuthenticatedTimesIfNeeded(beforePatch, entity);
        customerService.updateById(entity);
        entity = customerService.getOne(
                new QueryWrapper<DtCustomer>().lambda().eq(DtCustomer::getCusUserCode, userCode),
                false);

        // --- DTO 传播 ---
        DtCustomerDTO updatedDto = toCustomerDto(entity, userCode);
        if (publishProfileEvent) {
            publishCustomerProfileUpdated(userCode, updatedDto);
        }
        syncLocalProfileSideEffects(userCode, entity);
        return updatedDto;
    }

    /** 按用户编码加载客户，不存在则创建最小档案。 */
    private DtCustomer loadOrCreateCustomer(String userCode) {
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode);
        DtCustomer entity = customerService.getOne(queryWrapper, false);
        if (entity != null) {
            return entity;
        }
        entity = new DtCustomer();
        entity.setCusUserCode(userCode);
        entity.setCusIdentityAuthenticatedStatusCode(StatusCodeEnum.NO);
        entity.setCusHiddenStatusCode(StatusCodeEnum.NO);
        customerService.save(entity);
        return customerService.getOne(queryWrapper, false);
    }

    /** 请求体携带 cusCode 时须与当前登录客户一致（仅校验，不参与更新）。 */
    private static void validatePatchCusCode(DtCustomerDTO patchDto, DtCustomer entity) {
        String got = StrUtil.trim(patchDto.getCusCode());
        if (StrUtil.isBlank(got)) {
            return;
        }
        String expect = StrUtil.nullToDefault(entity.getCusCode(), "");
        if (StrUtil.isNotBlank(expect) && !StrUtil.equals(got, expect)) {
            throw new IllegalStateException("客户编码与当前登录不匹配");
        }
    }

    /** 将 patch DTO 中非空、可写字段合并到实体。 */
    private static void applyCustomerPatchToEntity(DtCustomerDTO patchDto, DtCustomer entity) {
        BeanUtil.copyProperties(patchDto, entity, CUSTOMER_PATCH_COPY_OPTIONS);
    }

    /** 实体转返回 DTO，并补齐 userCode。 */
    private static DtCustomerDTO toCustomerDto(DtCustomer entity, String userCode) {
        DtCustomerDTO dto = BeanUtil.copyProperties(entity, DtCustomerDTO.class);
        dto.setCusUserCode(StrUtil.trim(userCode));
        return dto;
    }

    /**
     * 发布 {@code dating.profile.updated}（事务提交后），供 dating 等域同步冗余客户快照。
     */
    private void publishCustomerProfileUpdated(String userCode, DtCustomer entity) {
        if (entity == null || StrUtil.isBlank(userCode)) {
            return;
        }
        publishCustomerProfileUpdated(userCode, toCustomerDto(entity, userCode));
    }

    private void publishCustomerProfileUpdated(String userCode, DtCustomerDTO customerDto) {
        if (customerDto == null || StrUtil.isBlank(userCode)) {
            return;
        }
        datingProfileUpdatedPublisher.publishAfterCommit(
                new DtProfileUpdatedMessage(userCode.trim(), customerDto));
    }

    /** 同进程副作用：系统用户头像/实名、IM 资料（非 MQ 广播）。 */
    private void syncLocalProfileSideEffects(String userCode, DtCustomer entity) {
        if (StrUtil.isNotEmpty(entity.getCusAvatar())) {
            apiSysUserService.updateAvatarByUserCode(userCode, entity.getCusAvatar());
        }
        syncUserRealNameFromCustomer(userCode, entity.getCusName());
        syncUserNickNameFromCustomer(userCode, entity.getCusNickName());
        syncImProfileFromCustomer(userCode, entity);
    }

    @Override
    public void notifyCustomerProfileUpdated(String userCode) {
        Assert.notBlank(userCode, "userCode 不能为空");
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode.trim());
        DtCustomer entity = customerService.getOne(queryWrapper, false);
        if (entity != null) {
            publishCustomerProfileUpdated(userCode.trim(), entity);
        }
    }

    @Override
    public void afterCustomerProfileFieldsUpdated(String userCode) {
        Assert.notBlank(userCode, "userCode 不能为空");
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode.trim());
        DtCustomer entity = customerService.getOne(queryWrapper, false);
        if (entity == null) {
            return;
        }
        syncLocalProfileSideEffects(userCode.trim(), entity);
        publishCustomerProfileUpdated(userCode.trim(), entity);
    }

    /** 规范化 patch DTO 中需特殊处理的字段。 */
    private static void normalizeCustomerPatch(DtCustomerDTO patchDto) {
        if (patchDto.getCusEducationCode() != null) {
            patchDto.setCusEducationCode(StrUtil.toStringOrNull(patchDto.getCusEducationCode()));
        }
        if (patchDto.getCusEducationName() != null) {
            patchDto.setCusEducationName(StrUtil.toStringOrNull(patchDto.getCusEducationName()));
        }
    }

    /**
     * 客户资料变更后同步腾讯云 IM（account_import / portrait_set）及本地 im_user。
     */
    private void syncImProfileFromCustomer(String userCode, DtCustomer entity) {
        if (entity == null || StrUtil.isBlank(userCode)) {
            return;
        }
        try {
            ImAccountDTO account = new ImAccountDTO();
            account.setUserCode(userCode.trim());
            account.setNickName(StrUtil.firstNonBlank(entity.getCusNickName(), entity.getCusName()));
            account.setAvatar(resolveCustomerAvatarForIm(entity.getCusAvatar()));
            account.setRealName(entity.getCusName());
            apiImService.saveOrUpdateAccount(account);
        }
        catch (Exception ex) {
            log.warn("同步 IM 用户资料失败, userCode={}", userCode, ex);
        }
    }

    /** cusAvatar 可能为逗号分隔多图，IM 头像取首图 */
    private static String resolveCustomerAvatarForIm(String cusAvatar) {
        if (StrUtil.isBlank(cusAvatar)) {
            return null;
        }
        String s = cusAvatar.trim();
        int comma = s.indexOf(',');
        if (comma > 0) {
            return s.substring(0, comma).trim();
        }
        return s;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean tryDeductAddFriendRight(String userCode, long amount) {
        MemberBenefitConsumeResultDTO result = tryConsumeMemberBenefit(
                userCode, CusMemberBenefitTypeEnum.ADD_FRIEND, amount, null);
        return result.isSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberBenefitConsumeResultDTO tryConsumeMemberBenefit(String userCode, CusMemberBenefitTypeEnum type,
                                                                  long amount, String bizRef) {
        Assert.notBlank(userCode, "userCode 不能为空");
        Assert.notNull(type, "权益类型不能为空");
        Assert.isTrue(amount > 0, "消费次数须为正数");

        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode.trim());
        DtCustomer entity = customerService.getOne(queryWrapper, false);
        if (entity == null) {
            return failByType(type);
        }

        long dailyLimit = readDayLimit(entity, type);
        long todayUsed = sumTodayConsumed(userCode.trim(), type);
        if (todayUsed + amount > dailyLimit) {
            return failByType(type);
        }

        DtMemberBenefitConsumeRecord record = new DtMemberBenefitConsumeRecord();
        record.setCusMbCstRecordCode(IdUtil.getSnowflakeNextIdStr());
        record.setUserCode(userCode.trim());
        record.setCusCode(entity.getCusCode());
        record.setBenefitTypeCode(type.getCode());
        record.setConsumeAmount(amount);
        record.setBizRef(StrUtil.blankToDefault(StrUtil.trim(bizRef), null));
        customerMemberBenefitConsumeRecordService.save(record);

        return MemberBenefitConsumeResultDTO.ok();
    }

    @Override
    public void enrichMemberBenefitDayUsage(DtCustomerDTO customer, String userCode) {
        if (customer == null || StrUtil.isBlank(userCode)) {
            return;
        }
        String normalized = userCode.trim();
        customer.setCusAddFriendDayUsed(sumTodayConsumed(normalized, CusMemberBenefitTypeEnum.ADD_FRIEND));
        customer.setCusRecommendDayUsed(sumTodayConsumed(normalized, CusMemberBenefitTypeEnum.RECOMMEND));
        customer.setCusMatchDayUsed(sumTodayConsumed(normalized, CusMemberBenefitTypeEnum.MATCH));
    }

    @Override
    public void enrichProfileCompletenessRate(DtCustomerDTO customer) {
        if (customer == null) {
            return;
        }
        customer.setCusProfileCompletenessRate(CustomerProfileCompletenessRateUtil.calc(customer));
    }

    private long sumTodayConsumed(String userCode, CusMemberBenefitTypeEnum type) {
        LocalDateTime dayStart = LocalDate.now().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        return customerMemberBenefitConsumeRecordService.lambdaQuery()
                .eq(DtMemberBenefitConsumeRecord::getUserCode, userCode)
                .eq(DtMemberBenefitConsumeRecord::getBenefitTypeCode, type.getCode())
                .ge(DtMemberBenefitConsumeRecord::getCreateTime, dayStart)
                .lt(DtMemberBenefitConsumeRecord::getCreateTime, dayEnd)
                .list()
                .stream()
                .mapToLong(row -> row.getConsumeAmount() == null ? 1L : row.getConsumeAmount())
                .sum();
    }

    private static long readDayLimit(DtCustomer entity, CusMemberBenefitTypeEnum type) {
        Long value = switch (type) {
            case ADD_FRIEND -> entity.getCusAddFriendDayLimit();
            case RECOMMEND -> entity.getCusRecommendDayLimit();
            case MATCH -> entity.getCusMatchDayLimit();
        };
        return value == null ? 0L : value;
    }

    private static void addDayLimit(DtCustomer entity, CusMemberBenefitTypeEnum type, long delta) {
        if (delta == 0) {
            return;
        }
        switch (type) {
            case ADD_FRIEND -> entity.setCusAddFriendDayLimit(safeAdd(entity.getCusAddFriendDayLimit(), delta));
            case RECOMMEND -> entity.setCusRecommendDayLimit(safeAdd(entity.getCusRecommendDayLimit(), delta));
            case MATCH -> entity.setCusMatchDayLimit(safeAdd(entity.getCusMatchDayLimit(), delta));
        }
    }

    private static MemberBenefitConsumeResultDTO failByType(CusMemberBenefitTypeEnum type) {
        CusMemberBenefitErrorCodeEnum code = switch (type) {
            case ADD_FRIEND -> CusMemberBenefitErrorCodeEnum.E1006;
            case MATCH -> CusMemberBenefitErrorCodeEnum.E1007;
            case RECOMMEND -> CusMemberBenefitErrorCodeEnum.E1008;
        };
        return MemberBenefitConsumeResultDTO.fail(code.getCode(), code.getDesc());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rechargeMemberBenefits(String tdOdGdCode, String tdOdCode, String userCode,
            Long addFriendRightDelta, Long recommendRightDelta, Long matchRightDelta) {
        Assert.notBlank(userCode, "userCode 不能为空");
        if (!willApplyMemberBenefitDeltas(addFriendRightDelta, recommendRightDelta, matchRightDelta)) {
            return;
        }
        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode);
        DtCustomer entity = customerService.getOne(queryWrapper, false);
        if (entity == null) {
            entity = new DtCustomer();
            entity.setCusUserCode(userCode);
            entity.setCusIdentityAuthenticatedStatusCode(StatusCodeEnum.NO);
            entity.setCusHiddenStatusCode(StatusCodeEnum.NO);
            customerService.save(entity);
            entity = customerService.getOne(queryWrapper, false);
        }
        Assert.notNull(entity, "客户记录不存在");

        if (StrUtil.isNotBlank(tdOdGdCode)) {
            DtMemberBenefitRechargeRecord record = new DtMemberBenefitRechargeRecord();
            record.setCusMbRchRecordCode(IdUtil.getSnowflakeNextIdStr());
            record.setTdOdGdCode(tdOdGdCode);
            record.setTdOdCode(StrUtil.blankToDefault(tdOdCode, null));
            record.setUserCode(userCode);
            record.setCusCode(entity.getCusCode());
            record.setCusAddFriendRightDelta(addFriendRightDelta);
            record.setCusRecommendRightDelta(recommendRightDelta);
            record.setCusMatchRightDelta(matchRightDelta);
            try {
                customerMemberBenefitRechargeRecordService.save(record);
            } catch (DuplicateKeyException ex) {
                log.info("member benefit recharge skipped, tdOdGdCode already processed: {}", tdOdGdCode);
                return;
            }
        }

        boolean dirty = false;
        if (addFriendRightDelta != null && addFriendRightDelta != 0) {
            addDayLimit(entity, CusMemberBenefitTypeEnum.ADD_FRIEND, addFriendRightDelta);
            dirty = true;
        }
        if (recommendRightDelta != null && recommendRightDelta != 0) {
            addDayLimit(entity, CusMemberBenefitTypeEnum.RECOMMEND, recommendRightDelta);
            dirty = true;
        }
        if (matchRightDelta != null && matchRightDelta != 0) {
            addDayLimit(entity, CusMemberBenefitTypeEnum.MATCH, matchRightDelta);
            dirty = true;
        }
        if (dirty) {
            customerService.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateMemberSubscription(String tdOdGdCode, String tdOdCode, String userCode,
                                           String memberTypeCode, String memberTypeName, Integer serviceDayPeriod) {
        Assert.notBlank(userCode, "userCode 不能为空");
        Assert.notBlank(memberTypeCode, "memberTypeCode 不能为空");
        String typeCode = memberTypeCode.trim();
        String typeName = StrUtil.blankToDefault(
                StrUtil.trim(memberTypeName),
                CusMemberTierConstants.resolveMemberTypeName(typeCode));
        TdGoodsMemberBenefitDeltaDTO benefitDelta = apiTdGoodsService.resolveMemberBenefitDelta(typeCode);
        long addFriendDelta = benefitDelta.getAddFriendNum();
        long recommendDelta = benefitDelta.getRecNum();
        long matchDelta = benefitDelta.getMatchNum();
        int days = CusMemberTierConstants.resolveServiceDays(serviceDayPeriod);

        QueryWrapper<DtCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DtCustomer::getCusUserCode, userCode.trim());
        DtCustomer entity = customerService.getOne(queryWrapper, false);
        if (entity == null) {
            entity = new DtCustomer();
            entity.setCusUserCode(userCode.trim());
            entity.setCusIdentityAuthenticatedStatusCode(StatusCodeEnum.NO);
            entity.setCusHiddenStatusCode(StatusCodeEnum.NO);
            customerService.save(entity);
            entity = customerService.getOne(queryWrapper, false);
        }
        Assert.notNull(entity, "客户记录不存在");

        if (StrUtil.isNotBlank(tdOdGdCode)) {
            DtMemberBenefitRechargeRecord record = new DtMemberBenefitRechargeRecord();
            record.setCusMbRchRecordCode(IdUtil.getSnowflakeNextIdStr());
            record.setTdOdGdCode(tdOdGdCode.trim());
            record.setTdOdCode(StrUtil.blankToDefault(tdOdCode, null));
            record.setUserCode(userCode.trim());
            record.setCusCode(entity.getCusCode());
            record.setCusAddFriendRightDelta(addFriendDelta);
            record.setCusRecommendRightDelta(recommendDelta);
            record.setCusMatchRightDelta(matchDelta);
            try {
                customerMemberBenefitRechargeRecordService.save(record);
            }
            catch (DuplicateKeyException ex) {
                log.info("member subscription skipped, tdOdGdCode already processed: {}", tdOdGdCode);
                return;
            }
        }

        LocalDateTime now = LocalDateTime.now();
        entity.setCusMemberTypeCode(typeCode);
        entity.setCusMemberTypeName(typeName);
        entity.setCusMemberExpireTime(now.plusDays(days));
        addDayLimit(entity, CusMemberBenefitTypeEnum.ADD_FRIEND, addFriendDelta);
        addDayLimit(entity, CusMemberBenefitTypeEnum.RECOMMEND, recommendDelta);
        addDayLimit(entity, CusMemberBenefitTypeEnum.MATCH, matchDelta);
        customerService.updateById(entity);
        publishCustomerProfileUpdated(userCode.trim(), entity);
        log.info("member subscription activated userCode={} typeCode={} days={} addFriend={} recommend={} match={}",
                userCode, typeCode, days, addFriendDelta, recommendDelta, matchDelta);
    }

    @Override
    public boolean applyIdentityAfterPhoneTwoFactorVerify(String userCode, String phone, String realName) {
        Assert.notBlank(userCode, "userCode 不能为空");
        String reqPhone = StrUtil.trim(phone);
        String reqName = StrUtil.trim(realName);
        Assert.notBlank(reqPhone, "phone 不能为空");
        Assert.notBlank(reqName, "realName 不能为空");

        QueryWrapper<DtCustomer> qw = new QueryWrapper<>();
        qw.lambda().eq(DtCustomer::getCusUserCode, userCode.trim());
        DtCustomer entity = customerService.getOne(qw, false);
        if (entity == null) {
            log.warn("applyIdentityAfterPhoneTwoFactorVerify: 未找到客户 userCode={}", userCode);
            return false;
        }
        if (StrUtil.isNotBlank(entity.getCusPhone())) {
            String cusPhone = StrUtil.trim(entity.getCusPhone());
            if (!reqPhone.equals(cusPhone)) {
                log.warn("applyIdentityAfterPhoneTwoFactorVerify: 客户手机号与核验手机号不一致 userCode={}", userCode);
                return false;
            }
        }
        entity.setCusIdentityAuthenticatedStatusCode(StatusCodeEnum.YES);
        entity.setCusIdentityAuthenticatedTime(LocalDateTime.now());
        entity.setCusName(reqName);
        if (StrUtil.isBlank(entity.getCusPhone())) {
            entity.setCusPhone(reqPhone);
        }
        customerService.updateById(entity);
        syncUserRealNameFromCustomer(userCode, entity.getCusName());
        syncUserIdentityAuthenticatedFromCustomer(userCode, StatusCodeEnum.YES);
        publishCustomerProfileUpdated(userCode, entity);
        return true;
    }

    private static boolean willApplyMemberBenefitDeltas(Long addFriendRightDelta, Long recommendRightDelta, Long matchRightDelta) {
        return (addFriendRightDelta != null && addFriendRightDelta != 0)
                || (recommendRightDelta != null && recommendRightDelta != 0)
                || (matchRightDelta != null && matchRightDelta != 0);
    }

    private static long safeAdd(Long current, long delta) {
        long base = current == null ? 0L : current;
        return base + delta;
    }

    /** 认证状态首次变为已认证时写入认证时间（已有时间则不覆盖）。 */
    private static void stampAuthenticatedTimesIfNeeded(DtCustomer before, DtCustomer entity) {
        if (before == null || entity == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        stampAuthTimeIfNewlyVerified(
                before.getCusHaveHouseStatusCode(), entity.getCusHaveHouseStatusCode(),
                entity.getCusHaveHouseAuthenticatedTime(), entity::setCusHaveHouseAuthenticatedTime, now);
        stampAuthTimeIfNewlyVerified(
                before.getCusHaveCarStatusCode(), entity.getCusHaveCarStatusCode(),
                entity.getCusHaveCarAuthenticatedTime(), entity::setCusHaveCarAuthenticatedTime, now);
        stampAuthTimeIfNewlyVerified(
                before.getCusIdentityAuthenticatedStatusCode(), entity.getCusIdentityAuthenticatedStatusCode(),
                entity.getCusIdentityAuthenticatedTime(), entity::setCusIdentityAuthenticatedTime, now);
        stampAuthTimeIfNewlyVerified(
                before.getCusMaritalStatusAuthenticatedStatusCode(), entity.getCusMaritalStatusAuthenticatedStatusCode(),
                entity.getCusMaritalStatusAuthenticatedTime(), entity::setCusMaritalStatusAuthenticatedTime, now);
    }

    private static void stampAuthTimeIfNewlyVerified(
            StatusCodeEnum beforeCode, StatusCodeEnum afterCode,
            LocalDateTime existingTime, java.util.function.Consumer<LocalDateTime> setter,
            LocalDateTime now) {
        if (existingTime != null) {
            return;
        }
        if (!isAuthYes(afterCode) || isAuthYes(beforeCode)) {
            return;
        }
        setter.accept(now);
    }

    private static boolean isAuthYes(StatusCodeEnum code) {
        return code != null && StatusCodeEnum.YES.equals(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyAssetCertApproved(String cusCode, String vehicleLicensePhoto, String realEstateCertificatePhoto) {
        Assert.notBlank(cusCode, "客户编码不能为空");
        DtCustomer customer = customerService.getByCode(cusCode.trim());
        Assert.notNull(customer, "客户不存在");
        LocalDateTime now = LocalDateTime.now();
        customer.setCusCarAssetCertStatusCode(StatusCodeEnum.YES);
        customer.setCusCarAssetCertTime(now);
        customer.setCusHouseAssetCertStatusCode(StatusCodeEnum.YES);
        customer.setCusHouseAssetCertTime(now);
        customer.setCusMaritalStatusAuthenticatedStatusCode(StatusCodeEnum.YES);
        customer.setCusMaritalStatusAuthenticatedTime(now);
        customer.setCusLsStatusCode(StatusCodeEnum.YES);
        if (StrUtil.isNotBlank(vehicleLicensePhoto)) {
            customer.setCusVehicleLicensePhoto(vehicleLicensePhoto.trim());
        }
        if (StrUtil.isNotBlank(realEstateCertificatePhoto)) {
            customer.setCusRealEstateCertificatePhoto(realEstateCertificatePhoto.trim());
        }
        customerService.updateById(customer);
        if (StrUtil.isNotBlank(customer.getCusUserCode())) {
            publishCustomerProfileUpdated(customer.getCusUserCode().trim(), customer);
        }
    }
}
