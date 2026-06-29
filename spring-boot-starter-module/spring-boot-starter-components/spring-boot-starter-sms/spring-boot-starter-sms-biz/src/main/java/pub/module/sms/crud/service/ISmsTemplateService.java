package pub.module.sms.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.sms.crud.entity.SmsTemplate;

import java.util.Collection;

public interface ISmsTemplateService extends IService<SmsTemplate> {

    SmsTemplate getByCode(String smsTemplateCode);

    boolean removeByBizCodes(Collection<String> smsTemplateCodes);
}
