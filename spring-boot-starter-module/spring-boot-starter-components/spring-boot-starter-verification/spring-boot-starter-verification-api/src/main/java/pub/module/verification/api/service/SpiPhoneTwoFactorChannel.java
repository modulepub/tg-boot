package pub.module.verification.api.service;

import pub.module.verification.api.dto.PhoneTwoFactorChannelOutcome;

/**
 * 二要素渠道插件契约：本仓库默认实现与三方扩展一视同仁，均依赖 verification-api 并实现本接口后注册为 Spring Bean。
 */
public interface SpiPhoneTwoFactorChannel {

    String getProviderCode();

    PhoneTwoFactorChannelOutcome verify(String phone, String realName);
}
