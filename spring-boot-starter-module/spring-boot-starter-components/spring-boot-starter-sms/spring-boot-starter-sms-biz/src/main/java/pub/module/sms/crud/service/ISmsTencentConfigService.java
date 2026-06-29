package pub.module.sms.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.sms.crud.entity.SmsTencentConfig;

import java.util.Collection;

/**
 * sms_tencent_config Service
 */
public interface ISmsTencentConfigService extends IService<SmsTencentConfig> {

    SmsTencentConfig getByCode(String smsTencentConfigCode);

    boolean removeByBizCodes(Collection<String> smsTencentConfigCodes);
}
