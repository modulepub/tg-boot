package pub.module.verification.biz.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyRequest;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;
import pub.module.verification.api.service.ApiPhoneTwoFactorVerifyService;

/**
 * 手机号二要素 HTTP 入口（需登录，与 /ocr 类似）
 */
@Tag(name = "工具-核验", description = "手机号姓名二要素")
@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class PhoneTwoFactorVerifyController {

    private final ApiPhoneTwoFactorVerifyService apiPhoneTwoFactorVerifyService;

    @Operation(summary = "手机号姓名二要素核验")
    @PostMapping("/phone-two-factor")
    public Result<PhoneTwoFactorVerifyResult> verify(@Valid @RequestBody PhoneTwoFactorVerifyRequest request) {
        enrichUserContext(request);
        return Result.ok(apiPhoneTwoFactorVerifyService.verify(request));
    }

    private static void enrichUserContext(PhoneTwoFactorVerifyRequest request) {
        if (StrUtil.isNotBlank(request.getNpRecordUserCode())) {
            return;
        }
        try {
            UserDTO user = UserUtil.getCurrentSysUser();
            if (user == null) {
                return;
            }
            request.setNpRecordUserCode(StrUtil.trim(user.getUserCode()));
            request.setNpRecordUserPhone(StrUtil.trim(user.getUserPhone()));
        } catch (Exception ignored) {
            // 匿名调用时不填充用户上下文
        }
    }
}
