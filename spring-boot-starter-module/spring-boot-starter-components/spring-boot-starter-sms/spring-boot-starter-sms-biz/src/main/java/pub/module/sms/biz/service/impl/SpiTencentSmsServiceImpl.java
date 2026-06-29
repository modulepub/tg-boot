package pub.module.sms.biz.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.sms.api.constants.SmsProviderCode;
import pub.module.sms.biz.service.SpiSmsService;
import pub.module.sms.biz.service.TencentSmsSender;

/**
 * 腾讯云短信 SPI 实现。
 */
@Slf4j
@Service
public class SpiTencentSmsServiceImpl implements SpiSmsService {

    @Override
    public String providerCode() {
        return SmsProviderCode.TENCENT.getCode();
    }

    @Resource
    private TencentSmsSender tencentSmsSender;

    @Override
    public String spiSendSms(SpiSendSmsDTO sendSmsDTO) {
        return tencentSmsSender.sendTemplateSms(
                sendSmsDTO.getMobile(),
                sendSmsDTO.getMobileList(),
                sendSmsDTO.getTemplateId(),
                sendSmsDTO.getTemplateParams(),
                sendSmsDTO.getSmsProviderCode());
    }
}
