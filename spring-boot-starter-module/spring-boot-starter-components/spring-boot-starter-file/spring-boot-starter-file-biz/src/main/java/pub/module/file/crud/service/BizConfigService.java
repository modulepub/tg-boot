package pub.module.file.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.file.crud.entity.BizConfig;

/**
 * CMS-节点 Service
 *
 * @author tg
 * 2026-03-21 21:34:38
 */
public interface BizConfigService extends IService<BizConfig> {
    BizConfig getByCode(String code);
}
