package pub.module.customer.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.customer.api.constants.CusSourceCodeEnum;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.entity.CustomerMemberBenefitRechargeRecord;
import pub.module.customer.curd.service.CustomerMemberBenefitRechargeRecordService;
import pub.module.customer.curd.service.CustomerService;
import pub.module.system.api.constants.UserSexCodeEnum;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.time.LocalDate;
import java.time.ZoneId;
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
public class ApiCustomerServiceImpl implements ApiCustomerService {
    @Resource
    CustomerService customerService;
    @Resource
    ApiSysUserService apiSysUserService;
    @Resource
    CustomerMemberBenefitRechargeRecordService customerMemberBenefitRechargeRecordService;

    /**
     * 不允许用户端通过该接口改写的字段（权益、审计、绑定关系等）
     */
    private static final Set<String> CUS_IMMUTABLE_FIELDS = Set.of(
            "id", "createBy", "createTime", "updateBy", "updateTime",
            "deleted", "version", "seqNo", "orgCode",
            "cusUserCode",
            "cusAddFriendRightValue", "cusRecommendRightValue", "cusMatchRightValue",
            "cusHandholdsNum"
    );

    @Override
    public void importData(Map<String, Object> data) {
        Customer customer = BeanUtil.copyProperties(data, Customer.class);
        customer.setCusSourceCode(CusSourceCodeEnum.IMPORT);
        log.info("导入客户数据数据{}", customer);
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusPhone, customer.getCusPhone()).or().eq(Customer::getCusIdNo, customer.getCusIdNo());
        Customer old = customerService.getOne(queryWrapper, false);
        if (old != null) {
            BeanUtil.copyProperties(old, customer, CopyOptions.create().setIgnoreNullValue(true));
            customerService.updateById(customer);
        } else {
            customerService.save(customer);
        }

    }

    @Override
    public void initCustomerByUser(UserDTO user) {
        Assert.notNull(user, "user 不能为空");
        String userCode = StrUtil.trim(user.getUserCode());
        Assert.notBlank(userCode, "user.userCode 不能为空");
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusUserCode, userCode);
        if (customerService.count(queryWrapper) > 0) {
            return;
        }
        Customer customer = new Customer();
        customer.setCusUserCode(userCode);
        customer.setCusIdentityAuthenticatedStatusCode("0");
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
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusUserCode, userCode);
        Customer customer = customerService.getOne(queryWrapper, false);
        if (customer == null) {
            return;
        }
        if (nickName.equals(StrUtil.trim(customer.getCusNickName()))) {
            return;
        }
        LambdaUpdateWrapper<Customer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Customer::getCusUserCode, userCode).set(Customer::getCusNickName, nickName);
        customerService.update(updateWrapper);
        log.info("已同步客户昵称 userCode={} cusNickName={}", userCode, nickName);
    }

    @Override
    public CustomerDTO getCusByUserCode(String userCode) {
        Assert.notBlank(userCode, "userCode 不能为空");
        initCustomerByUser(new UserDTO().setUserCode(userCode));
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusUserCode, userCode);
        Customer customer = customerService.getOne(queryWrapper, false);
        return BeanUtil.copyProperties(customer, CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> listAll(List<String> notIn) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().notIn(Customer::getCusCode, notIn);
        List<Customer> customerList = customerService.list(queryWrapper);
        return BeanUtil.copyToList(customerList, CustomerDTO.class);
    }

    @Override
    public CustomerDTO getCusByCusCode(String cusCode) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusCode, cusCode);
        Customer customer = customerService.getOne(queryWrapper, false);
        return BeanUtil.copyProperties(customer,CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> findCustomer(List<String> notIn, CustomerDTO customerDTO) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        if(notIn!=null&&!notIn.isEmpty()){
            queryWrapper.lambda().notIn(Customer::getCusCode, notIn);
        }
        if(customerDTO.getCusSexCode()!=null){
            queryWrapper.lambda().eq(Customer::getCusSexCode,customerDTO.getCusSexCode().getCode());
        }
        List<Customer> customerList = customerService.list(queryWrapper);
        return BeanUtil.copyToList(customerList,CustomerDTO.class);
    }

    @Override
    public CustomerDTO getCusByNickNameExact(String cusNickName) {
        String nick = StrUtil.trim(cusNickName);
        if (StrUtil.isBlank(nick)) {
            return null;
        }
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusNickName, nick).last("LIMIT 1");
        Customer customer = customerService.getOne(queryWrapper, false);
        if (customer == null) {
            return null;
        }
        return BeanUtil.copyProperties(customer, CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> listByCusCodes(Collection<String> cusCodes) {
        if (cusCodes == null || cusCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> codes = cusCodes.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Customer::getCusCode, codes);
        List<Customer> list = customerService.list(queryWrapper);
        return BeanUtil.copyToList(list, CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> listByUserCodes(Collection<String> userCodes) {
        if (userCodes == null || userCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> codes = userCodes.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Customer::getCusUserCode, codes);
        List<Customer> list = customerService.list(queryWrapper);
        return BeanUtil.copyToList(list, CustomerDTO.class);
    }

    @Override
    public CustomerDTO updateCurrCustomerPartial(String userCode, Map<String, Object> patchRaw) {
        Assert.notBlank(userCode, "用户未登录");
        Map<String, Object> patch = patchRaw == null ? Collections.emptyMap() : new LinkedHashMap<>(patchRaw);
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusUserCode, userCode);
        Customer entity = customerService.getOne(queryWrapper, false);
        if (entity == null) {
            entity = new Customer();
            entity.setCusUserCode(userCode);
            entity.setCusIdentityAuthenticatedStatusCode("0");
            customerService.save(entity);
            entity = customerService.getOne(queryWrapper, false);
        }
        Object cusCodeInBody = patch.remove("cusCode");
        if (cusCodeInBody != null) {
            String got = StrUtil.toStringOrNull(cusCodeInBody);
            String expect = StrUtil.nullToDefault(entity.getCusCode(), "");
            if (StrUtil.isNotBlank(expect) && StrUtil.isNotBlank(got) && !StrUtil.equals(got, expect)) {
                throw new IllegalStateException("客户编码与当前登录不匹配");
            }
        }
        for (String k : CUS_IMMUTABLE_FIELDS) {
            patch.remove(k);
        }
        normalizeCustomerPatchDates(patch);
        Object ageRaw = patch.get("cusAge");
        if (ageRaw instanceof Number && !(ageRaw instanceof Long)) {
            patch.put("cusAge", ((Number) ageRaw).longValue());
        }
        normalizeCustomerPatchStringFields(patch);
        normalizeCustomerPatchSexCode(patch);
        BeanWrapperImpl bw = new BeanWrapperImpl(entity);
        for (Map.Entry<String, Object> e : patch.entrySet()) {
            String key = e.getKey();
            if (!bw.isWritableProperty(key)) {
                log.debug("skip unknown customer field: {}", key);
                continue;
            }
            try {
                bw.setPropertyValue(key, e.getValue());
            }
            catch (Exception ex) {
                log.warn("skip field {} : {}", key, ex.getMessage());
            }
        }
        customerService.updateById(entity);
        if(StrUtil.isNotEmpty(entity.getCusAvatar())){
            apiSysUserService.updateAvatarByUserCode(userCode,entity.getCusAvatar());
        }
        return BeanUtil.copyProperties(entity, CustomerDTO.class);
    }

    /**
     * 用户端 patch 中 cusBirthday 常为 JSON 字符串（yyyy-MM-dd / yyyy-MM），BeanWrapper 无法直接写入 Date，此处先规范化。
     */
    private static void normalizeCustomerPatchDates(Map<String, Object> patch) {
        Object v = patch.get("cusBirthday");
        if (v == null) {
            return;
        }
        if (v instanceof Date) {
            return;
        }
        String s = StrUtil.toStringOrNull(v);
        if (StrUtil.isBlank(s)) {
            patch.remove("cusBirthday");
            return;
        }
        s = s.trim();
        try {
            LocalDate ld;
            if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
                ld = LocalDate.parse(s.substring(0, 10));
            }
            else if (s.matches("\\d{4}-\\d{1,2}")) {
                String[] p = s.split("-");
                ld = LocalDate.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]), 1);
            }
            else {
                log.warn("skip cusBirthday parse, unsupported format: {}", s);
                patch.remove("cusBirthday");
                return;
            }
            Date d = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
            patch.put("cusBirthday", d);
        }
        catch (Exception ex) {
            log.warn("skip cusBirthday parse: {} -> {}", s, ex.getMessage());
            patch.remove("cusBirthday");
        }
    }

    /** 用户端 patch 中 cusSexCode 常为字符串 "1"/"2"，需转为 {@link UserSexCodeEnum} 再写入实体。 */
    private static void normalizeCustomerPatchSexCode(Map<String, Object> patch) {
        Object v = patch.get("cusSexCode");
        if (v == null) {
            return;
        }
        if (v instanceof UserSexCodeEnum) {
            return;
        }
        UserSexCodeEnum parsed = UserSexCodeEnum.fromJson(v);
        if (parsed != null) {
            patch.put("cusSexCode", parsed);
        } else {
            patch.remove("cusSexCode");
            log.warn("skip cusSexCode, unsupported value: {}", v);
        }
    }

    /** 将 patch 中应为字符串的字段转为 String，避免 BeanWrapper 跳过写入（如整型/其他类型）。 */
    private static void normalizeCustomerPatchStringFields(Map<String, Object> patch) {
        Object eduCode = patch.get("cusEducationCode");
        if (eduCode != null && !(eduCode instanceof String)) {
            patch.put("cusEducationCode", StrUtil.toStringOrNull(eduCode));
        }
        Object eduName = patch.get("cusEducationName");
        if (eduName != null && !(eduName instanceof String)) {
            patch.put("cusEducationName", StrUtil.toStringOrNull(eduName));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rechargeMemberBenefits(String tdOdGdCode, String tdOdCode, String userCode,
            Long addFriendRightDelta, Long recommendRightDelta, Long matchRightDelta) {
        Assert.notBlank(userCode, "userCode 不能为空");
        if (!willApplyMemberBenefitDeltas(addFriendRightDelta, recommendRightDelta, matchRightDelta)) {
            return;
        }
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Customer::getCusUserCode, userCode);
        Customer entity = customerService.getOne(queryWrapper, false);
        if (entity == null) {
            entity = new Customer();
            entity.setCusUserCode(userCode);
            entity.setCusIdentityAuthenticatedStatusCode("0");
            customerService.save(entity);
            entity = customerService.getOne(queryWrapper, false);
        }
        Assert.notNull(entity, "客户记录不存在");

        if (StrUtil.isNotBlank(tdOdGdCode)) {
            CustomerMemberBenefitRechargeRecord record = new CustomerMemberBenefitRechargeRecord();
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
            entity.setCusAddFriendRightValue(safeAdd(entity.getCusAddFriendRightValue(), addFriendRightDelta));
            dirty = true;
        }
        if (recommendRightDelta != null && recommendRightDelta != 0) {
            entity.setCusRecommendRightValue(safeAdd(entity.getCusRecommendRightValue(), recommendRightDelta));
            dirty = true;
        }
        if (matchRightDelta != null && matchRightDelta != 0) {
            entity.setCusMatchRightValue(safeAdd(entity.getCusMatchRightValue(), matchRightDelta));
            dirty = true;
        }
        if (dirty) {
            customerService.updateById(entity);
        }
    }

    private static final String IDENTITY_AUTHENTICATED_YES = "1";

    @Override
    public boolean applyIdentityAfterPhoneTwoFactorVerify(String userCode, String phone, String realName) {
        Assert.notBlank(userCode, "userCode 不能为空");
        String reqPhone = StrUtil.trim(phone);
        String reqName = StrUtil.trim(realName);
        Assert.notBlank(reqPhone, "phone 不能为空");
        Assert.notBlank(reqName, "realName 不能为空");

        QueryWrapper<Customer> qw = new QueryWrapper<>();
        qw.lambda().eq(Customer::getCusUserCode, userCode.trim());
        Customer entity = customerService.getOne(qw, false);
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
        entity.setCusIdentityAuthenticatedStatusCode(IDENTITY_AUTHENTICATED_YES);
        entity.setCusName(reqName);
        if (StrUtil.isBlank(entity.getCusPhone())) {
            entity.setCusPhone(reqPhone);
        }
        customerService.updateById(entity);
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
}
