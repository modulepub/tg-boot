package pub.module.system.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.BizCaptchaService;
import pub.module.system.api.vo.*;
import pub.module.web.vo.Result;


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
public class PubCaptchaController {

    @Resource
    BizCaptchaService bizCaptchaService;

    @GetMapping("/captcha")
    @Operation(summary = "验证码")
    public Result<SysCaptchaVO> captcha() {
        SysCaptchaVO captchaVO = bizCaptchaService.generate();
        return Result.ok(captchaVO);
    }

    @GetMapping("/captcha/enabled")
    @Operation(summary = "是否开启验证码")
    public Result<Boolean> captchaEnabled() {
        boolean enabled = bizCaptchaService.isCaptchaEnabled();

        return Result.ok(enabled);
    }
}
