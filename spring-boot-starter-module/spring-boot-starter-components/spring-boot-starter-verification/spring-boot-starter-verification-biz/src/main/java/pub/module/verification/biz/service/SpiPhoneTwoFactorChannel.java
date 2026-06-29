package pub.module.verification.biz.service;

import pub.module.verification.api.dto.PhoneTwoFactorChannelOutcome;

/**
 * 二要素渠道 SPI（模块内多实现，由 {@link pub.module.verification.biz.service.impl.ApiPhoneTwoFactorVerifyServiceImpl} 路由）。
 */
public interface SpiPhoneTwoFactorChannel {

    String getProviderCode();

    PhoneTwoFactorChannelOutcome verify(String phone, String realName);
}
