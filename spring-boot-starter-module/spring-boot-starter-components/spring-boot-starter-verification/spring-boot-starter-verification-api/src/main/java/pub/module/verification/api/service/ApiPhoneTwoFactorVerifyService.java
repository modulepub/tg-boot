package pub.module.verification.api.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyRequest;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;

/**
 * 手机号二要素核验（供各业务模块依赖 *-api 调用）
 */
@Validated
public interface ApiPhoneTwoFactorVerifyService {

    /**
     * 执行一次核验并写入 {@code vt_np_record}
     */
    PhoneTwoFactorVerifyResult verify(@NotNull @Valid PhoneTwoFactorVerifyRequest request);
}
