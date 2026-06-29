package pub.module.sms.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.sms.crud.entity.SmsSendLog;

public interface ISmsSendLogService extends IService<SmsSendLog> {

    SmsSendLog getByCode(String smsSendLogCode);
}
