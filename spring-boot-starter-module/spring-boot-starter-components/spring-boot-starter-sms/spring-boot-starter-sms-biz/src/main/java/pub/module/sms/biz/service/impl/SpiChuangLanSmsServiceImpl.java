package pub.module.sms.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.sms.api.util.SmsChuangLanSender;

import jakarta.annotation.Resource;
import pub.module.sms.biz.service.SpiSmsService;

@Slf4j
@Service("chuangLan")
public class SpiChuangLanSmsServiceImpl implements SpiSmsService {
    @Resource
    private SmsChuangLanSender smsChuangLanSender;

    @Override
    public void spiSendSms(SpiSendSmsDTO sendSmsDTO) {
        smsChuangLanSender.sendSms(sendSmsDTO.getMobile(), sendSmsDTO.getContent());
    }
}
