package pub.module.sms.api.service;

import pub.module.sms.api.dto.SendSmsDTO;

/**
 * 短信发送 API（供其他模块跨模块调用，按 providerCode 路由 SPI 实现）。
 */
public interface ApiSmsSendService {

    /**
     * 发送短信。
     *
     * @param dto 渠道编码、手机号、模板/内容等
     * @return 渠道返回的发送流水号（若渠道支持）
     */
    String sendSms(SendSmsDTO dto);
}
