package pub.module.config.curd.service;

import pub.module.config.curd.entity.BizConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * CMS-节点 Service
 *
 * @author tg
 * 2026-03-21 21:34:38
 */
public interface BizConfigService extends IService<BizConfig> {
    BizConfig getByCode(String code);
}
