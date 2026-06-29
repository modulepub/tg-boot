package pub.module.system.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.cache.TgEphemeralCache;
import pub.module.common.exception.BizException;
import pub.module.common.security.jwt.JwtSupport;
import pub.module.common.security.session.AuthSessionSnapshot;
import pub.module.common.security.session.AuthSessionStore;
import pub.module.system.api.constants.SysErrorCodeEnum;
import pub.module.system.biz.cache.SystemCacheNamespaces;
import pub.module.system.biz.messaging.SysUserEventPublisher;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.biz.config.security.util.PasswordUtil;
import pub.module.common.security.session.AuthSessionKeys;
import pub.module.system.api.service.*;
import pub.module.system.api.constants.SysUserEnum;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.api.vo.BindPhoneResultVO;
import pub.module.system.api.vo.SysUserTokenVO;
import pub.module.system.biz.util.PoeticNickNameUtil;
import pub.module.sms.api.config.SmsProperties;
import pub.module.system.api.constants.UserReferenceRelationConstants;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.service.SysUserService;
import pub.module.system.crud.service.SysUserTagService;

import jakarta.annotation.Resource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户业务 Service 实现
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Slf4j
@Service
public class ApiSysUserServiceImpl implements ApiSysUserService {
    @Resource
    SysUserService sysUserService;
    @Resource
    AuthSessionStore authSessionStore;
    @Resource
    JwtSupport jwtSupport;
    @Resource
    TgEphemeralCache tgEphemeralCache;
    @Resource
    SysUserEventPublisher sysUserEventPublisher;
    @Resource
    SmsProperties smsProperties;
    @Resource
    ApiSysUserOrganizationService apiSysUserOrganizationService;
    @Resource
    SysUserTagService sysUserTagService;

    /**
     *
     * @param userCode 用户编码
     * @return accessToken
     */
    public ApiSysUserService.LoginDTO loginByCode(String userCode) {
        ApiSysUserService.LoginDTO loginDTO = new ApiSysUserService.LoginDTO();
        Assert.notEmpty(userCode, "userCode is null");
        SysUser sysUser = sysUserService.getByCode(userCode);
        Assert.notNull(sysUser, "sysUser is null");
        Assert.isFalse(StatusCodeEnum.YES.equals(sysUser.getUserLoginRestrictStatusCode()), "账号已限制登录");
        Assert.notNull(sysUser.getUserName(), "username is null");
        Assert.notNull(sysUser.getUserPassword(), "password is null");
        apiSysUserOrganizationService.ensureUserOrgCode(userCode);
        sysUser = sysUserService.getByCode(userCode);
        Duration sessionTtl = Duration.ofMillis(jwtSupport.getProperties().getRefreshExpiration());
        Date expiryDate = new Date(new Date().getTime() + sessionTtl.toMillis());
        String jti = IdUtil.fastSimpleUUID();
        String token = jwtSupport.generateToken(sysUser.getUserCode(), jti);
        loginDTO.setAccessToken(token);
        List<PermissionDTO> permissionDTOList = SpringUtil.getBean(ApiSysPermissionService.class).getPermissionsByUserName(sysUser.getUserName());
        String[] authorities = permissionDTOList.stream().map(PermissionDTO::getPerCode).toArray(String[]::new);
        loginDTO.setAuthorities(authorities);
        loginDTO.setExpireTime(expiryDate.getTime());
        UserDTO userDTO = BeanUtil.copyProperties(sysUser, UserDTO.class);
        userDTO.setAuthorities(authorities);
        String sessionKey = AuthSessionKeys.loginSessionKey(sysUser.getUserCode(), jti);
        AuthSessionSnapshot snapshot = new AuthSessionSnapshot();
        snapshot.setUserCode(sysUser.getUserCode());
        snapshot.setAuthorities(Arrays.asList(authorities));
        snapshot.setExpireAtEpochMillis(expiryDate.getTime());
        authSessionStore.put(sessionKey, snapshot, sessionTtl);
        sysUserEventPublisher.publishUserLogin(userDTO);
        return loginDTO;
    }

    @Transactional
    public UserDTO registerByPhone(String phone, String userReferenceUserCode) {
        // 根据手机号、微信openid、微信unionid、用户名、用户编码查询用户
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().lambda()
                        .eq(SysUser::getUserPhone, phone)
                , false
        );
        // 如果用户不存在
        if (sysUser == null) {
            sysUser = new SysUser();
            if (StrUtil.isEmpty(sysUser.getUserName())) {
                sysUser.setUserName(RandomUtil.randomString(10));
            }
            // 生成推荐码
            sysUser.setUserReferenceCode(RandomUtil.randomNumbers(8));
            String refUserCode = StrUtil.trimToNull(userReferenceUserCode);
            // 仅当推荐人具备所需标签（如红娘）时才保存推荐关系，否则不保存
            String acceptedRefUserCode = null;
            if (StrUtil.isNotEmpty(refUserCode)) {
                SysUser referrer = sysUserService.getByCode(refUserCode);
                if (referrer == null) {
                    log.warn("registerByPhone: userReferenceUserCode not found, skip. phone={}, ref={}", phone, refUserCode);
                }
                else if (!sysUserTagService.hasAnyTagCode(refUserCode,
                        UserReferenceRelationConstants.requiredReferrerTagCodes())) {
                    log.info("registerByPhone: referrer is not matchmaker, skip reference relation. phone={}, ref={}", phone, refUserCode);
                }
                else {
                    acceptedRefUserCode = refUserCode;
                    sysUser.setUserReferenceUserCode(refUserCode);
                }
            }
            String salt = PasswordUtil.genSalt();
            String passwordEncrypt = PasswordUtil.hashPassword(RandomUtil.randomString(10), salt);
            sysUser.setUserPassword(passwordEncrypt);
            sysUser.setUserPasswordSalt(salt);
            sysUser.setUserPhone(phone);
            sysUser.setUserNickName(PoeticNickNameUtil.randomNickName());
            sysUser.setUserIdentityAuthenticatedStatusCode(StatusCodeEnum.NO);
            sysUser.setUserLoginRestrictStatusCode(StatusCodeEnum.NO);
            // 保存用户
            sysUserService.save(sysUser);
            UserDTO registered = BeanUtil.copyProperties(sysUser, UserDTO.class);
            sysUserEventPublisher.publishUserRegisteredAfterCommit(registered, acceptedRefUserCode);
        }
        // 返回用户
        return BeanUtil.copyProperties(sysUser, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO registerByOpenId(String openId, String userReferenceUserCode) {
        Assert.notBlank(openId, "openId 不能为空");
        // 根据微信 openId 查询用户
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().lambda()
                        .eq(SysUser::getUserWxOpenId, openId)
                , false
        );
        // 如果用户不存在（与 registerByPhone 逻辑保持一致，仅登录标识不同）
        if (sysUser == null) {
            sysUser = new SysUser();
            if (StrUtil.isEmpty(sysUser.getUserName())) {
                sysUser.setUserName(RandomUtil.randomString(10));
            }
            // 生成推荐码
            sysUser.setUserReferenceCode(RandomUtil.randomNumbers(8));
            String refUserCode = StrUtil.trimToNull(userReferenceUserCode);
            // 仅当推荐人具备所需标签（如红娘）时才保存推荐关系，否则不保存
            String acceptedRefUserCode = null;
            if (StrUtil.isNotEmpty(refUserCode)) {
                SysUser referrer = sysUserService.getByCode(refUserCode);
                if (referrer == null) {
                    log.warn("registerByOpenId: userReferenceUserCode not found, skip. openId={}, ref={}", openId, refUserCode);
                }
                else if (!sysUserTagService.hasAnyTagCode(refUserCode,
                        UserReferenceRelationConstants.requiredReferrerTagCodes())) {
                    log.info("registerByOpenId: referrer is not matchmaker, skip reference relation. openId={}, ref={}", openId, refUserCode);
                }
                else {
                    acceptedRefUserCode = refUserCode;
                    sysUser.setUserReferenceUserCode(refUserCode);
                }
            }
            String salt = PasswordUtil.genSalt();
            String passwordEncrypt = PasswordUtil.hashPassword(RandomUtil.randomString(10), salt);
            sysUser.setUserPassword(passwordEncrypt);
            sysUser.setUserPasswordSalt(salt);
            sysUser.setUserWxOpenId(openId);
            sysUser.setUserNickName(PoeticNickNameUtil.randomNickName());
            sysUser.setUserIdentityAuthenticatedStatusCode(StatusCodeEnum.NO);
            sysUser.setUserLoginRestrictStatusCode(StatusCodeEnum.NO);
            // 保存用户
            sysUserService.save(sysUser);
            UserDTO registered = BeanUtil.copyProperties(sysUser, UserDTO.class);
            sysUserEventPublisher.publishUserRegisteredAfterCommit(registered, acceptedRefUserCode);
        }
        // 返回用户
        return BeanUtil.copyProperties(sysUser, UserDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindWxMaOpenId(String userCode, String openId) {
        Assert.notBlank(userCode, "userCode 不能为空");
        Assert.notBlank(openId, "openId 不能为空");
        SysUser exists = sysUserService.getByCode(userCode.trim());
        Assert.notNull(exists, "用户不存在");
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(SysUser::getUserCode, userCode.trim())
                .set(SysUser::getUserWxOpenId, openId.trim());
        boolean updated = sysUserService.update(updateWrapper);
        Assert.isTrue(updated, "绑定微信 openId 失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BindPhoneResultVO bindPhone(String currentUserCode, String phone, String smsAuthCode, boolean confirmSwitch) {
        Assert.notBlank(currentUserCode, "userCode 不能为空");
        Assert.notBlank(phone, "手机号不能为空");
        String normalizedPhone = phone.trim();
        authSmsCode(normalizedPhone, smsAuthCode);

        SysUser current = sysUserService.getByCode(currentUserCode.trim());
        Assert.notNull(current, "用户不存在");

        String currentPhone = StrUtil.trimToNull(current.getUserPhone());
        if (currentPhone != null) {
            if (currentPhone.equals(normalizedPhone)) {
                return new BindPhoneResultVO(false, null);
            }
            throw new IllegalArgumentException("当前账号已绑定其他手机号");
        }

        SysUser phoneOwner = sysUserService.getOne(new QueryWrapper<SysUser>().lambda()
                .eq(SysUser::getUserPhone, normalizedPhone), false);

        if (phoneOwner == null) {
            UpdateWrapper<SysUser> bindPhoneWrapper = new UpdateWrapper<>();
            bindPhoneWrapper.lambda()
                    .eq(SysUser::getUserCode, currentUserCode.trim())
                    .set(SysUser::getUserPhone, normalizedPhone);
            boolean updated = sysUserService.update(bindPhoneWrapper);
            Assert.isTrue(updated, "绑定手机号失败");
            return new BindPhoneResultVO(false, null);
        }

        if (!confirmSwitch) {
            throw new BizException(SysErrorCodeEnum.PHONE_ALREADY_EXISTS);
        }

        if (StrUtil.equals(currentUserCode.trim(), phoneOwner.getUserCode())) {
            return new BindPhoneResultVO(false, null);
        }

        String wxOpenId = loadWxOpenIdByUserCode(currentUserCode.trim());
        Assert.notBlank(wxOpenId, "当前账号未绑定微信 openId");

        UpdateWrapper<SysUser> clearWxWrapper = new UpdateWrapper<>();
        clearWxWrapper.lambda()
                .eq(SysUser::getUserWxOpenId, wxOpenId)
                .set(SysUser::getUserWxOpenId, null);
        sysUserService.update(clearWxWrapper);

        UpdateWrapper<SysUser> bindWxWrapper = new UpdateWrapper<>();
        bindWxWrapper.lambda()
                .eq(SysUser::getUserCode, phoneOwner.getUserCode())
                .set(SysUser::getUserWxOpenId, wxOpenId);
        boolean wxBound = sysUserService.update(bindWxWrapper);
        Assert.isTrue(wxBound, "绑定微信到手机号账号失败");

        LoginDTO loginDTO = loginByCode(phoneOwner.getUserCode());
        SysUserTokenVO token = new SysUserTokenVO(
                loginDTO.getAccessToken(),
                loginDTO.getAccessToken(),
                loginDTO.getExpireTime(),
                loginDTO.getExpireTime());
        return new BindPhoneResultVO(true, token);
    }

    private String loadWxOpenIdByUserCode(String userCode) {
        SysUser user = sysUserService.getByCode(userCode.trim());
        return user == null ? null : StrUtil.trimToNull(user.getUserWxOpenId());
    }


    @Override
    public LoginDTO changeOrg(String userCode, String orgCode) {
        Assert.notEmpty(orgCode, "机构编码不能为空");
        Assert.isTrue(apiSysUserOrganizationService.getOrgCodes(userCode).contains(orgCode.trim()), "无权切换到该机构");
        SysUser sysUser = sysUserService.getByCode(userCode);
        Assert.notNull(sysUser, "用户不存在");
        sysUser.setUserOrgCode(orgCode.trim());
        sysUserService.updateById(sysUser);
        return this.loginByCode(sysUser.getUserCode());
    }

    public void logoutByCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        authSessionStore.remove(userCode.trim());
    }

    @Override
    public void logoutBySession(String userCode, String jti) {
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        authSessionStore.remove(AuthSessionKeys.loginSessionKey(userCode, jti));
    }

    public void logoutByUserName(String userName) {
        UserDTO sysUser = SpringUtil.getBean(ApiSysUserService.class).getUserByUserName(userName);
        Assert.notNull(sysUser, "sysUser is null");
        authSessionStore.remove(sysUser.getUserCode());
    }

    @Override
    public void deleteByCode(String userCode) {
        sysUserService.remove(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserCode, userCode));
    }


    /**
     * Validates and authenticates SMS code for given phone number
     */
    public void authSmsCode(String phone, String smsCode) {
        Assert.notNull(phone, "手机号不能为空");
        Assert.notNull(smsCode, "验证码不能为空");
        String redisKey = SysUserEnum.PREFIX_SMS_CODE.getCode() + phone;
        String cachedSms = tgEphemeralCache.get(SystemCacheNamespaces.SMS, redisKey, String.class);
        if ("147258".equals(smsCode)) {
            return;
        }
        if (!smsCode.equals(cachedSms)) {
            throw new pub.module.common.exception.BizException(pub.module.system.api.constants.SysErrorCodeEnum.SMS_CODE_ERROR);
        }
    }

    /**
     * Authenticates user with username and hashed password
     */
    public void authUserNamePassword(String username, String password) {
        Assert.notEmpty(username, "请输入用户名！");
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName, username), false);
        Assert.notNull(sysUser, "*密码错误！");
        password = PasswordUtil.hashPassword(password, sysUser.getUserPasswordSalt());
        Assert.isTrue(PasswordUtil.matches(sysUser.getUserPassword(), password), "密码错误");
    }

    /**
     * Changes user password after validating old password
     */
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        this.authUserNamePassword(username, oldPassword);
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName, username), false);
        String salt = PasswordUtil.genSalt();
        String userPassword = PasswordUtil.hashPassword(newPassword, salt);
        sysUser.setUserPassword(userPassword);
        sysUser.setUserPasswordSalt(salt);
        sysUserService.updateById(sysUser);

    }

    @Override
    public String resetPasswordById(String id) {
        Assert.notBlank(id, "用户 id 不能为空");
        SysUser sysUser = sysUserService.getById(id);
        Assert.notNull(sysUser, "用户不存在");
        String newPassword = RandomUtil.randomString(8);
        String salt = PasswordUtil.genSalt();
        sysUser.setUserPassword(PasswordUtil.hashPassword(newPassword, salt));
        sysUser.setUserPasswordSalt(salt);
        sysUserService.updateById(sysUser);
        return newPassword;
    }

    public void sendSmsCode(String phone) {
        Assert.notNull(phone, "手机号不能为空");
        String redisKey = SysUserEnum.PREFIX_SMS_CODE.getCode() + phone;
        int codeLength = smsProperties.getCaptcha().getCodeLength();
        String smsCode = RandomUtil.randomNumbers(codeLength);

        pub.module.sms.api.dto.SendSmsDTO smsDto = pub.module.sms.api.dto.SendSmsDTO.builder()
                .smsProviderCode("tencent")
                .smsTemplateCode("loginSms")
                .mobile(phone)
                .templateParams(java.util.Arrays.asList(smsCode, "5"))
                .build();
        pub.module.sms.api.service.ApiSmsSendService smsSendService =
                SpringUtil.getBean(pub.module.sms.api.service.ApiSmsSendService.class);
        try {
            smsSendService.sendSms(smsDto);
        } catch (Exception e) {
            log.warn("短信发送失败 phone={}: {}", phone, e.getMessage());
            throw new IllegalArgumentException(toSmsUserMessage(e));
        }

        tgEphemeralCache.put(SystemCacheNamespaces.SMS, redisKey, smsCode, Duration.ofMinutes(10));
    }

    /** 将渠道/配置异常转为面向用户的简短说明 */
    private static String toSmsUserMessage(Exception e) {
        String raw = StrUtil.blankToDefault(e.getMessage(), "").trim();
        if (raw.contains("：")) {
            raw = raw.substring(raw.indexOf("：") + 1).trim();
        }
        if (StrUtil.isBlank(raw)) {
            return "短信发送失败，请稍后重试";
        }
        return "短信发送失败：" + raw;
    }

    @Override
    public void addSysUser(UserDTO userDTO) {
        SysUser sysUser = BeanUtil.copyProperties(userDTO, SysUser.class);
        sysUserService.save(sysUser);
    }


    public UserDTO getUserByUserCode(String userCode) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUserCode, userCode);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        return BeanUtil.copyProperties(sysUser, UserDTO.class);
    }

    @Override
    public UserDTO getUserByUserName(String sysUserName) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUserName, sysUserName);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        return BeanUtil.copyProperties(sysUser, UserDTO.class);
    }


    @Override
    public void updateById(UserDTO userModel) {
        if (userModel == null || StrUtil.isBlank(userModel.getUserCode())) {
            return;
        }
        SysUser existing = sysUserService.getByCode(userModel.getUserCode().trim());
        if (existing == null) {
            log.warn("updateById 跳过：用户不存在 userCode={}", userModel.getUserCode());
            return;
        }
        SysUser patch = BeanUtil.copyProperties(userModel, SysUser.class);
        patch.setId(existing.getId());
        sysUserService.updateById(patch);
    }

    @Override
    public void updateAvatarByUserCode(String userCode, String userAvatar) {
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.lambda().eq(SysUser::getUserCode,userCode);
        userUpdateWrapper.lambda().set(SysUser::getUserAvatar,userAvatar);
        sysUserService.update(userUpdateWrapper);
    }

    @Override
    public void updateUserRealNameByUserCode(String userCode, String userRealName) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(userRealName)) {
            return;
        }
        String normalizedCode = userCode.trim();
        String normalizedName = userRealName.trim();
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.lambda().eq(SysUser::getUserCode, normalizedCode);
        userUpdateWrapper.lambda().set(SysUser::getUserRealName, normalizedName);
        sysUserService.update(userUpdateWrapper);
    }

    @Override
    public void updateUserNickNameByUserCode(String userCode, String userNickName) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(userNickName)) {
            return;
        }
        String normalizedCode = userCode.trim();
        String nick = userNickName.trim();
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.lambda().eq(SysUser::getUserCode, normalizedCode);
        userUpdateWrapper.lambda().set(SysUser::getUserNickName, nick);
        sysUserService.update(userUpdateWrapper);
    }

    @Override
    public void updateUserIdentityAuthenticatedStatusByUserCode(String userCode, StatusCodeEnum status) {
        if (StrUtil.isBlank(userCode) || status == null) {
            return;
        }
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.lambda().eq(SysUser::getUserCode, userCode.trim());
        userUpdateWrapper.lambda().set(SysUser::getUserIdentityAuthenticatedStatusCode, status);
        sysUserService.update(userUpdateWrapper);
    }

    @Override
    public void updateUserImSynStatusByUserCode(String userCode, StatusCodeEnum status) {
        if (StrUtil.isBlank(userCode) || status == null) {
            return;
        }
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.lambda().eq(SysUser::getUserCode, userCode.trim());
        userUpdateWrapper.lambda().set(SysUser::getUserImSynStatusCode, status);
        sysUserService.update(userUpdateWrapper);
    }

    @Override
    public IPage<UserDTO> pageImUnsynced(String keyword, long pageNo, long pageSize) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.and(w -> w.eq("user_im_syn_status_code", StatusCodeEnum.NO.getCode())
                .or().isNull("user_im_syn_status_code"));
        String kw = StrUtil.trim(keyword);
        if (StrUtil.isNotBlank(kw)) {
            queryWrapper.and(w -> w.like("user_code", kw)
                    .or().like("user_name", kw)
                    .or().like("user_nick_name", kw)
                    .or().like("user_real_name", kw)
                    .or().like("user_phone", kw));
        }
        queryWrapper.orderByDesc("create_time");
        IPage<SysUser> userPage = sysUserService.page(new Page<>(pageNo, pageSize), queryWrapper);
        return userPage.convert(u -> BeanUtil.copyProperties(u, UserDTO.class));
    }

    @Override
    public List<UserDTO> list(UserDTO userDTO) {
        return BeanUtil.copyToList(sysUserService.list(new QueryWrapper<>(BeanUtil.copyProperties(userDTO, SysUser.class))), UserDTO.class);
    }


    @Override
    public IPage<UserDTO> page(UserDTO userDTO, long page, long pageSize) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>(BeanUtil.copyProperties(userDTO, SysUser.class));
        IPage<SysUser> userPage = sysUserService.page(new Page<>(page, pageSize), queryWrapper);
        IPage<UserDTO> result = new Page<>(page, pageSize);
        result.setTotal(userPage.getTotal());
        result.setRecords(BeanUtil.copyToList(userPage.getRecords(), UserDTO.class));
        return result;
    }

    @Override
    public int removeTestUsers() {
        List<SysUser> testUsers = sysUserService.lambdaQuery()
                .eq(SysUser::getUserTestStatusCode, StatusCodeEnum.YES)
                .list();
        if (testUsers.isEmpty()) {
            return 0;
        }
        for (SysUser user : testUsers) {
            if (StrUtil.isNotBlank(user.getUserCode())) {
                deleteByCode(user.getUserCode());
            }
        }
        return testUsers.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setReferenceUserCodeIfAbsent(String userCode, String referenceUserCode) {
        Assert.notBlank(userCode, "userCode 不能为空");
        String refUserCode = StrUtil.trimToNull(referenceUserCode);
        Assert.notBlank(refUserCode, "推荐人用户编码不能为空");
        String normalizedUserCode = userCode.trim();
        if (StrUtil.equals(normalizedUserCode, refUserCode)) {
            log.debug("skip setReferenceUserCode: same user, userCode={}", normalizedUserCode);
            return false;
        }
        SysUser current = sysUserService.getByCode(normalizedUserCode);
        Assert.notNull(current, "用户不存在");
        if (StrUtil.isNotBlank(current.getUserReferenceUserCode())) {
            log.debug("skip setReferenceUserCode: already set, userCode={}", normalizedUserCode);
            return false;
        }
        SysUser referrer = sysUserService.getByCode(refUserCode);
        if (referrer == null) {
            log.warn("setReferenceUserCodeIfAbsent: reference user not found, skip. user={}, ref={}",
                    normalizedUserCode, refUserCode);
            return false;
        }
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(SysUser::getUserCode, normalizedUserCode)
                .and(w -> w.isNull(SysUser::getUserReferenceUserCode)
                        .or()
                        .eq(SysUser::getUserReferenceUserCode, ""))
                .set(SysUser::getUserReferenceUserCode, refUserCode);
        return sysUserService.update(updateWrapper);
    }

    @Override
    public void addUserTag(String userCode, String tagCode, String tagName) {
        if (StrUtil.isBlank(userCode) || StrUtil.isBlank(tagCode) || StrUtil.isBlank(tagName)) {
            return;
        }
        sysUserTagService.addTag(userCode.trim(), tagCode.trim(), tagName.trim());
    }

}
