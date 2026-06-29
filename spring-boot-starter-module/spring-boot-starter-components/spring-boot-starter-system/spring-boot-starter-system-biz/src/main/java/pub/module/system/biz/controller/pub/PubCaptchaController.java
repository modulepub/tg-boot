package pub.module.system.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.ApiCaptchaService;
import pub.module.system.api.vo.*;
import pub.module.common.model.vo.Result;


/**
 * 公开-认证管理
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@RequestMapping("/pub/auth")
@Tag(name = "公开-免鉴权-验证码")
@AllArgsConstructor
public class PubCaptchaController {

    @Resource
    ApiCaptchaService apiCaptchaService;

    @GetMapping("/captcha")
    @Operation(summary = "公开-免鉴权-验证码")
    public Result<SysCaptchaVO> captcha() {
        SysCaptchaVO captchaVO = apiCaptchaService.generate();
        return Result.ok(captchaVO);
    }

    @GetMapping("/captcha/enabled")
    @Operation(summary = "公开-免鉴权-是否开启验证码")
    public Result<Boolean> captchaEnabled() {
        boolean enabled = apiCaptchaService.isCaptchaEnabled();

        return Result.ok(enabled);
    }
}
