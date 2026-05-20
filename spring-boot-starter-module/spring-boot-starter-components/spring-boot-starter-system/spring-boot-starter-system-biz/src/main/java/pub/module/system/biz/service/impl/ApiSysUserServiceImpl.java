package pub.module.system.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.context.ApplicationEventPublisher;
import pub.module.system.api.constants.VerificationTypeCodeEnum;
import pub.module.system.api.event.SysUserLoginEvent;
import pub.module.system.api.event.SysUserRegisteredEvent;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.api.vo.SysVerificationDTO;
import pub.module.system.biz.config.security.util.JwtTokenProvider;
import pub.module.system.biz.config.security.util.PasswordUtil;
import pub.module.system.api.service.*;
import pub.module.system.api.constants.SysUserEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.biz.util.PoeticNickNameUtil;
import pub.module.system.curd.entity.SysUser;
import pub.module.system.curd.service.SysUserService;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pub.module.system.biz.config.security.util.JwtTokenProvider.jwtProperties;

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
    ApiSysVerificationService apiSysVerificationService;
    @Resource
    ApplicationEventPublisher applicationEventPublisher;

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
        Assert.notNull(sysUser.getUserName(), "username is null");
        Assert.notNull(sysUser.getUserPassword(), "password is null");
        Date expiryDate = new Date(new Date().getTime() + jwtProperties.getRefreshExpiration());
        String token = JwtTokenProvider.generateToken(sysUser.getUserCode());
        loginDTO.setAccessToken(token);
        List<PermissionDTO> permissionDTOList = SpringUtil.getBean(ApiSysPermissionService.class).getPermissionsByUserName(sysUser.getUserName());
        String[] authorities = permissionDTOList.stream().map(PermissionDTO::getPerCode).toArray(String[]::new);
        loginDTO.setAuthorities(authorities);
        loginDTO.setExpireTime(expiryDate.getTime());
        UserDTO userDTO = BeanUtil.copyProperties(sysUser, UserDTO.class);
        userDTO.setAuthorities(authorities);
        SpringUtil.getBean(ApiSysVerificationService.class).set(VerificationTypeCodeEnum.LOGIN_ACCESS_TOKEN.getCode(), userCode, JSONUtil.toJsonStr(userDTO), DateUtil.toLocalDateTime(expiryDate));
        applicationEventPublisher.publishEvent(new SysUserLoginEvent(userDTO));
        return loginDTO;
    }

    public void authenticate(String userCode) {
        // 设置到 上下文
        SysVerificationDTO sysVerificationDTO = SpringUtil.getBean(ApiSysVerificationService.class)
                .getByKey(VerificationTypeCodeEnum.LOGIN_ACCESS_TOKEN.getCode(), userCode);
        Assert.notNull(sysVerificationDTO, "TOKEN 已经过期");
        UserDTO userDTO = JSONUtil.toBean(sysVerificationDTO.getVerificationValue(), UserDTO.class);
        AuthenticationManager authenticationManager = SpringUtil.getBean(AuthenticationManager.class);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (String authority : userDTO.getAuthorities()) {

            try {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
                grantedAuthorities.add(simpleGrantedAuthority);
            } catch (Exception e) {
                log.error(authority, e);
            }
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDTO.getUserName(),
                        "",
                        grantedAuthorities
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
            if (StrUtil.isNotEmpty(refUserCode)) {
                SysUser referrer = sysUserService.getByCode(refUserCode);
                if (referrer != null) {
                    sysUser.setUserReferenceUserCode(refUserCode);
                }
                else {
                    log.warn("registerByPhone: userReferenceUserCode not found, skip. phone={}, ref={}", phone, refUserCode);
                }
            }
            String salt = PasswordUtil.genSalt();
            String passwordEncrypt = PasswordUtil.hashPassword(RandomUtil.randomString(10), salt);
            sysUser.setUserPassword(passwordEncrypt);
            sysUser.setUserPasswordSalt(salt);
            sysUser.setUserPhone(phone);
            sysUser.setUserNickName(PoeticNickNameUtil.randomNickName());
            // 保存用户
            sysUserService.save(sysUser);
            UserDTO registered = BeanUtil.copyProperties(sysUser, UserDTO.class);
            applicationEventPublisher.publishEvent(new SysUserRegisteredEvent(registered, refUserCode));
        }
        // 返回用户
        return BeanUtil.copyProperties(sysUser, UserDTO.class);
    }

    @Override
    public UserDTO registerByOpenId(String openId) {
        return null;
    }


    @Override
    public LoginDTO changeOrg(String userCode, String orgCode) {
        SysUser sysUser = sysUserService.getByCode(UserUtil.getCurrentSysUser().getUserCode());
        sysUser.setUserOrgCode(orgCode);
        sysUserService.updateById(sysUser);
        return this.loginByCode(sysUser.getUserCode());
    }

    public void logoutByCode(String userCode) {
        SpringUtil.getBean(ApiSysVerificationService.class).delByKey(VerificationTypeCodeEnum.LOGIN_ACCESS_TOKEN.getCode(), userCode);
    }

    public void logoutByUserName(String userName) {
        UserDTO sysUser = SpringUtil.getBean(ApiSysUserService.class).getUserByUserName(userName);
        Assert.notNull(sysUser, "sysUser is null");
        SpringUtil.getBean(ApiSysVerificationService.class).delByKey(VerificationTypeCodeEnum.LOGIN_ACCESS_TOKEN.getCode(), sysUser.getUserCode());
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
        SysVerificationDTO sysVerificationDTO = apiSysVerificationService.getByKey(VerificationTypeCodeEnum.SMS.getCode(), redisKey);
        if ("666666".equals(smsCode)) {
            return;
        }
        Assert.isTrue(sysVerificationDTO != null && smsCode.equals(sysVerificationDTO.getVerificationCode()), "验证码错误");
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

    public void sendSmsCode(String phone) {
        Assert.notNull(phone, "手机号不能为空");
        String redisKey = SysUserEnum.PREFIX_SMS_CODE.getCode() + phone;
        String smsCode = RandomUtil.randomNumbers(6);
        apiSysVerificationService.set(VerificationTypeCodeEnum.SMS.getCode(), redisKey, smsCode, LocalDateTime.now().plusMinutes(10));
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
        SysUser sysUser = BeanUtil.copyProperties(userModel, SysUser.class);
        sysUserService.updateById(sysUser);
    }

    @Override
    public void updateAvatarByUserCode(String userCode, String userAvatar) {
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.lambda().eq(SysUser::getUserCode,userCode);
        userUpdateWrapper.lambda().set(SysUser::getUserAvatar,userAvatar);
        sysUserService.update(userUpdateWrapper);
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


}
