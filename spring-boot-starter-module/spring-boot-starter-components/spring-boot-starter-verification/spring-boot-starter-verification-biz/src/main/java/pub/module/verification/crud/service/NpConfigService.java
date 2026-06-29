package pub.module.verification.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.verification.crud.entity.NpConfig;

import java.util.Collection;

/**
 * vt_np_config Service
 */
public interface NpConfigService extends IService<NpConfig> {

    NpConfig getByCode(String npConfigCode);

    boolean removeByBizCodes(Collection<String> npConfigCodes);
}
