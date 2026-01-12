package pub.module.system.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.security.api.util.PasswordUtil;
import pub.module.system.api.service.BizCaptchaService;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.vo.SysUserTokenVO;
import pub.module.web.vo.Result;

import java.io.Serializable;


/**
 * 认证管理 Controller
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@RequestMapping("/pub/auth")
@Tag(name = "认证管理")
@AllArgsConstructor
@Slf4j
public class PubLoginController {

    @Resource
    BizCaptchaService bizCaptchaService;
    @Resource
    BizSysUserService bizSysUserService;



    @Data
    @Schema(description = "账号登录")
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
    @Operation(summary = "账号密码登录")
    public Result<SysUserTokenVO> login(@RequestBody SysAccountLoginVO loginVO) {
        bizCaptchaService.validate(loginVO.getKey(), loginVO.getCaptcha());
        String password = PasswordUtil.decryptPassword(loginVO.getPassword());
        bizSysUserService.authUserNamePassword(loginVO.getUsername(), password);
        UserDTO userDTO = bizSysUserService.getUserByUserName(loginVO.getUsername());
        BizSysUserService.LoginDTO loginDTO = bizSysUserService.loginByCode(userDTO.getUserCode());
        SysUserTokenVO token = new SysUserTokenVO(loginDTO.getAccessToken(),loginDTO.getAccessToken(), loginDTO.getExpireTime(), loginDTO.getExpireTime());
        return Result.ok(token);
    }


    @Data
    @Schema(description = "手机号登录 VO")
    public static class LoginByPhoneVO {
        @Schema(description = "手机号")
        public String phone;
        @Schema(description = "验证码")
        public String smsAuthCode;
        @Schema(description = "来源渠道")
        public String source;

    }

    @Operation(summary = "手机号登录接口")
    @PostMapping("/phoneLogin")
    public Result<SysUserTokenVO> phoneLogin(@RequestBody LoginByPhoneVO loginByPhone) {
            bizSysUserService.authSmsCode(loginByPhone.getPhone(), loginByPhone.getSmsAuthCode());
            UserDTO result = bizSysUserService.registerByPhone(loginByPhone.getPhone());
            BizSysUserService.LoginDTO loginDTO = bizSysUserService.loginByCode(result.getUserCode());
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

    @Operation(summary = "发送短信接口")
    @PostMapping("/sendSms")
    public Result<String> phoneLogin(@RequestBody SendSms sendSms) {
        log.info("发送短信参数{}", sendSms);
        bizSysUserService.sendSmsCode(sendSms.getPhone());
        return Result.ok("发送成功，默认密码888888，不要使用后端返回的信息提示用户，不方便做国际化");
    }

}
