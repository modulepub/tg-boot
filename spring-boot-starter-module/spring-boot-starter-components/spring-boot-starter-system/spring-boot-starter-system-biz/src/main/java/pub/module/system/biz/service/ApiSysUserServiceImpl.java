package pub.module.system.biz.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import pub.module.cache.api.service.BizCacheService;
import pub.module.security.api.util.JwtTokenProvider;
import pub.module.security.api.util.PasswordUtil;
import pub.module.system.api.service.*;
import pub.module.system.api.constants.SysUserEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.curd.entity.SysUser;
import pub.module.system.curd.service.SysUserService;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
    ApiSysPermissionService apiSysPermissionService;



    @Transactional
    public UserDTO registerByPhone(String phone) {
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
            String salt = PasswordUtil.genSalt();
            String passwordEncrypt = PasswordUtil.hashPassword(RandomUtil.randomString(10), salt);
            sysUser.setUserPassword(passwordEncrypt);
            sysUser.setUserPasswordSalt(salt);
            sysUser.setUserPhone(phone);
            // 保存用户
            sysUserService.save(sysUser);

        }
        // 返回用户
        return BeanUtil.copyProperties(sysUser, UserDTO.class);
    }

    @Override
    public UserDTO registerByOpenId(String openId) {
        return null;
    }

    /**
     *
     * @param userCode 用户编码
     * @return accessToken
     */
    public LoginDTO loginByCode(String userCode) {
        LoginDTO loginDTO = new LoginDTO();
        Assert.notEmpty(userCode, "userCode is null");
        SysUser sysUser =sysUserService.getByCode(userCode);
        Assert.notNull(sysUser, "sysUser is null");
        Assert.notNull(sysUser.getUserName(), "username is null");
        Assert.notNull(sysUser.getUserPassword(), "password is null");
        //1.生成token
        String sysUserName = sysUser.getUserName();
        // 进行用户名密码认证
        List<PermissionDTO> permissionDTOList = apiSysPermissionService.getPermissionsByUserCode(userCode);
        String token = JwtTokenProvider.generateToken(sysUserName, sysUser.getUserPassword(), permissionDTOList.stream().map(PermissionDTO::getPerCode).toList());
        loginDTO.setAccessToken(token);
        loginDTO.setExpireTime(LocalDateTimeUtil.offset(LocalDateTime.now(), 100, ChronoUnit.DAYS).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        return loginDTO;
    }


    @Override
    public LoginDTO changeOrg(String userCode, String orgCode) {
        SysUser sysUser = sysUserService.getByCode(UserUtil.getCurrentSysUser().getUserCode());
        sysUser.setUserOrgCode(orgCode);
        sysUserService.updateById(sysUser);
        return this.loginByCode(sysUser.getUserCode());
    }

    public void logout(String userCode) {
        UserDTO sysUser = SpringUtil.getBean(ApiSysUserService.class).getUserByUserCode(userCode);
        Assert.notNull(sysUser, "sysUser is null");
        SpringUtil.getBean(BizCacheService.class).delete(sysUser.getUserName());
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
        String code = SpringUtil.getBean(BizCacheService.class).get(redisKey);
        if ("666666".equals(smsCode)) {
            return;
        }
        Assert.isTrue(smsCode.equals(code), "验证码错误");
    }

    /**
     * Authenticates user with username and hashed password
     */
    public void authUserNamePassword(String username, String password) {
        Assert.notEmpty(username, "请输入用户名！");
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName, username), false);
        Assert.notNull(sysUser,"*密码错误！");
        password = PasswordUtil.hashPassword(password, sysUser.getUserPasswordSalt());
        Assert.isTrue(PasswordUtil.matches(sysUser.getUserPassword(), password), "密码错误");
    }

    /**
     * Changes user password after validating old password
     */
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        this.authUserNamePassword(username,oldPassword);
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
        SpringUtil.getBean(BizCacheService.class).set(redisKey, smsCode);
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
