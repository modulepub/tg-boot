package pub.module.ai.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.ai.crud.entity.AiApiConfig;

import java.util.Collection;

public interface IAiApiConfigService extends IService<AiApiConfig> {

    AiApiConfig getByCode(String aiApiConfigCode);

    /** 获取首个已启用的接口配置（智能体未指定配置时使用）。 */
    AiApiConfig getFirstEnabled();

    boolean removeByBizCodes(Collection<String> aiApiConfigCodes);
}
