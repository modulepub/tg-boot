package pub.module.sms.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.stereotype.Service;
import pub.module.sms.api.service.BizSmsService;
import pub.module.sms.biz.service.SpiSmsService;

@Service
public class BizSmsServiceImpl implements BizSmsService {
    @Override
    public void sendSms(SendSmsDTO sendSmsDTO) {
        SpiSmsService spiSmsService = SpringUtil.getBean(sendSmsDTO.getPlateCode(),SpiSmsService.class);
        spiSmsService.spiSendSms(BeanUtil.copyProperties(sendSmsDTO, SpiSmsService.SpiSendSmsDTO.class));
    }
}
