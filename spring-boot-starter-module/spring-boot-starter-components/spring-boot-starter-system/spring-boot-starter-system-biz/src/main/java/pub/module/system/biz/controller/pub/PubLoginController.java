package pub.module.system.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.util.log.EasyLog;
import pub.module.system.biz.config.security.util.PasswordUtil;
import pub.module.system.api.service.ApiCaptchaService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.vo.SysUserTokenVO;
import pub.module.common.model.vo.Result;

import java.io.Serializable;


/**
 * 公开-认证管理
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@RequestMapping("/pub/auth")
@Tag(name = "公开-免鉴权-登录相关接口")
@AllArgsConstructor
@Slf4j
public class PubLoginController {

    @Resource
    ApiCaptchaService apiCaptchaService;
    @Resource
    ApiSysUserService apiSysUserService;


    @Data
    @Schema(description = "免鉴权-账号登录")
    public static class SysAccountLoginVO implements Serializable {

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "密码")
        private String password;

        @Schema(description = "唯一 key")
        private String key;

        @Schema(description = "验证码")
        private String captcha;
    }

    @PostMapping("/login")
    @Operation(summary = "公开-免鉴权-账号密码登录")
    public Result<SysUserTokenVO> login(@RequestBody SysAccountLoginVO loginVO) {
        apiCaptchaService.validate(loginVO.getKey(), loginVO.getCaptcha());
        String password = PasswordUtil.decryptPassword(loginVO.getPassword());
        apiSysUserService.authUserNamePassword(loginVO.getUsername(), password);
        UserDTO userDTO = apiSysUserService.getUserByUserName(loginVO.getUsername());
        ApiSysUserService.LoginDTO loginDTO = apiSysUserService.loginByCode(userDTO.getUserCode());
        SysUserTokenVO token = new SysUserTokenVO(loginDTO.getAccessToken(),loginDTO.getAccessToken(), loginDTO.getExpireTime(), loginDTO.getExpireTime());
        EasyLog.record("账号密码登录","登录成功","用户："+loginVO.getUsername(), userDTO.getUserCode());
        return Result.ok(token);
    }


    @Data
    @Schema(description = "免鉴权-手机号登录VO")
    public static class LoginByPhoneVO {
        @Schema(description = "手机号")
        public String phone;
        @Schema(description = "验证码")
        public String smsAuthCode;
        @Schema(description = "来源渠道")
        public String source;
        @Schema(description = "分享人用户编码（首次登录注册时传入）")
        public String userReferenceUserCode;

    }

    @Operation(summary = "公开-免鉴权-手机号登录接口")
    @PostMapping("/phoneLogin")
    public Result<SysUserTokenVO> phoneLogin(@RequestBody LoginByPhoneVO loginByPhone) {
            apiSysUserService.authSmsCode(loginByPhone.getPhone(), loginByPhone.getSmsAuthCode());
            UserDTO result = apiSysUserService.registerByPhone(
                    loginByPhone.getPhone(),
                    loginByPhone.getUserReferenceUserCode());
            ApiSysUserService.LoginDTO loginDTO = apiSysUserService.loginByCode(result.getUserCode());
            SysUserTokenVO token = new SysUserTokenVO(loginDTO.getAccessToken(),loginDTO.getAccessToken(), loginDTO.getExpireTime(), loginDTO.getExpireTime());
            return Result.ok(token);
    }

    @Data
    @Schema(description = "发送短信 VO")
    public static class SendSms {
        @Schema(description = "手机号")
        public String phone;
        public String captchaKey;
    }

    @Operation(summary = "公开-免鉴权-发送短信接口")
    @PostMapping("/sendSms")
    public Result<String> phoneLogin(@RequestBody SendSms sendSms) {
        log.info("发送短信参数{}", sendSms);
        apiSysUserService.sendSmsCode(sendSms.getPhone());
        return Result.ok("发送成功，默认密码888888，不要使用后端返回的信息提示用户，不方便做国际化");
    }

}
