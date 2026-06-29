package pub.module.system.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import pub.module.common.security.jwt.JwtSupport;
import pub.module.system.biz.messaging.SysUserEventPublisher;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.vo.BindPhoneResultVO;
import pub.module.system.api.vo.SysUserTokenVO;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.service.SysUserService;
import pub.module.common.model.vo.Result;
import pub.module.system.api.util.UserUtil;
import pub.module.wx.api.service.ApiWxMaSessionService;

import java.io.Serializable;


/**
 * 用户端-系统用户
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Tag(name = "用户端-用户")
@RestController
@RequestMapping("/cus/sysUser")
@Slf4j
public class CusSysUserController {
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private JwtSupport jwtSupport;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserEventPublisher sysUserEventPublisher;
    @Resource
    private ApiWxMaSessionService apiWxMaSessionService;

    @Data
    public static class BindWxMaOpenIdVO {
        @Schema(description = "小程序 appId")
        private String appId;
        @Schema(description = "wx.login 返回的 code")
        private String code;
    }

    @Operation(summary = "用户端-绑定微信小程序 openId")
    @PostMapping(value = "/bindWxMaOpenId")
    public Result<String> bindWxMaOpenId(@RequestBody BindWxMaOpenIdVO vo) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        if (vo == null || StrUtil.isBlank(vo.getAppId()) || StrUtil.isBlank(vo.getCode())) {
            return Result.error("appId 或 code 不能为空");
        }
        String openId = apiWxMaSessionService.getOpenIdByCode(vo.getAppId().trim(), vo.getCode().trim());
        apiSysUserService.bindWxMaOpenId(userDTO.getUserCode(), openId);
        return Result.ok("绑定成功");
    }

    @Data
    public static class BindPhoneVO {
        @Schema(description = "手机号")
        private String phone;
        @Schema(description = "短信验证码")
        private String smsAuthCode;
        @Schema(description = "手机号已被占用时，用户确认将微信绑定到该手机号并切换登录")
        private Boolean confirmSwitch;
    }

    @Operation(summary = "用户端-绑定手机号（短信验证）")
    @PostMapping(value = "/bindPhone")
    public Result<BindPhoneResultVO> bindPhone(@RequestBody BindPhoneVO vo) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        if (vo == null || StrUtil.isBlank(vo.getPhone()) || StrUtil.isBlank(vo.getSmsAuthCode())) {
            return Result.error("手机号或验证码不能为空");
        }
        boolean confirmSwitch = Boolean.TRUE.equals(vo.getConfirmSwitch());
        BindPhoneResultVO result = apiSysUserService.bindPhone(
                userDTO.getUserCode(),
                vo.getPhone().trim(),
                vo.getSmsAuthCode().trim(),
                confirmSwitch);
        return Result.ok(result);
    }

    @Operation(summary = "用户端-获取当前登录用户信息")
    @GetMapping(value = "/getInfo")
    public Result<UserDTO> getLoginUser() {
        UserDTO loginUser = UserUtil.getCurrentSysUser();
        return Result.ok(loginUser);
    }

    @Data
    public static class EditUserInfoVO {
        String userRealName;
        String userPhone;
        String userSexCode;
        String userAvatar;
        /**
         * 用户昵称
         */
        @Schema(description = "用户端-用户昵称")
        private String userNickName;

    }

    @Operation(summary = "用户端-编辑用户信息")
    @PostMapping(value = "/editUserInfo")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> editUserInfo(@RequestBody EditUserInfoVO editUserInfoVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        SysUser sysUser = sysUserService.getByCode(userDTO.getUserCode());
        if (editUserInfoVO.getUserAvatar() != null) {
            sysUser.setUserAvatar(editUserInfoVO.getUserAvatar());
        }
        boolean nickTouched = editUserInfoVO.getUserNickName() != null;
        if (nickTouched) {
            String nick = StrUtil.trim(editUserInfoVO.getUserNickName());
            if (StrUtil.isBlank(nick)) {
                return Result.error("昵称不能为空");
            }
            SysUser duplicate = sysUserService.lambdaQuery()
                    .eq(SysUser::getUserNickName, nick)
                    .ne(SysUser::getUserCode, userDTO.getUserCode())
                    .last("LIMIT 1")
                    .one();
            if (duplicate != null) {
                return Result.error("该昵称已被使用，请换一个");
            }
            sysUser.setUserNickName(nick);
        }
        sysUserService.updateById(sysUser);
        if (nickTouched && StrUtil.isNotBlank(userDTO.getUserCode())) {
            sysUserEventPublisher.publishUserInfoUpdatedAfterCommit(
                    new UserDTO()
                            .setUserCode(userDTO.getUserCode())
                            .setUserNickName(sysUser.getUserNickName()));
        }
        return Result.ok("编辑用户信息!");
    }

    @Data
    public static class ChangePasswordVO {
        String oldPassword;
        String newPassword;
    }

    @Operation(summary = "用户端-修改密码")
    @PostMapping(value = "/changePassword")
    public Result<String> changePassword(@RequestBody ChangePasswordVO changePasswordVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        apiSysUserService.changePassword(userDTO.getUserName(), changePasswordVO.getOldPassword(), changePasswordVO.getNewPassword());
        return Result.ok("修改密码成功!");
    }


    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "用户端-获取用户信息")
    public static class GetInfoByCodeVO implements Serializable {
        @Schema(description = "用户端-用户编码")
        private java.lang.String userCode;
    }

    @Operation(summary = "用户端-获取用户信息-通过用户编码")
    @GetMapping(value = "/getInfoByCode")
    public Result<UserDTO> getInfoByCode(GetInfoByCodeVO getInfoByCodeVO) {
        UserDTO result = apiSysUserService.getUserByUserCode(getInfoByCodeVO.getUserCode());
        return Result.ok(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户端-退出")
    public Result<String> logout(HttpServletRequest request) {
        String token = resolveBearerToken(request);
        if (StringUtils.hasText(token)) {
            try {
                DecodedJWT decodedJWT = jwtSupport.verify(token);
                apiSysUserService.logoutBySession(
                        decodedJWT.getClaim("userCode").asString(),
                        decodedJWT.getId());
                return Result.ok();
            }
            catch (Exception e) {
                log.warn("logout by token failed, fallback to current user: {}", e.getMessage());
            }
        }
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        apiSysUserService.logoutByCode(userDTO.getUserCode());
        return Result.ok();
    }

    private String resolveBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtSupport.getProperties().getHeaderName());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtSupport.getProperties().getTokenPrefix())) {
            return bearerToken.substring(jwtSupport.getProperties().getTokenPrefix().length()).trim();
        }
        return null;
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "用户端-获取accessToken")
    public Result<SysUserTokenVO> token(String refreshToken) {
        ApiSysUserService.LoginDTO loginDTO = apiSysUserService.loginByCode(UserUtil.getCurrentSysUser().getUserCode());
        SysUserTokenVO token = new SysUserTokenVO(loginDTO.getAccessToken(), loginDTO.getAccessToken(), loginDTO.getExpireTime(), loginDTO.getExpireTime());
        return Result.ok(token);
    }

    @Data
    public static class SetReferenceUserCodeVO {
        @Schema(description = "推荐人用户编码（被关注人 userCode）")
        private String userReferenceUserCode;
    }

    @Operation(summary = "用户端-设置推荐人（仅当前无推荐人时生效）")
    @PostMapping(value = "/setReferenceUserCodeIfAbsent")
    public Result<String> setReferenceUserCodeIfAbsent(@RequestBody SetReferenceUserCodeVO vo) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        if (vo == null || StrUtil.isBlank(vo.getUserReferenceUserCode())) {
            return Result.error("推荐人用户编码不能为空");
        }
        apiSysUserService.setReferenceUserCodeIfAbsent(userDTO.getUserCode(), vo.getUserReferenceUserCode().trim());
        return Result.ok("设置成功");
    }
}
