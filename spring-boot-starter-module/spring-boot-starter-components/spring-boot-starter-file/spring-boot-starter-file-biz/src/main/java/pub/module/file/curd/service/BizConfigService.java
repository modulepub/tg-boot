package pub.module.file.curd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.file.curd.entity.BizConfig;

/**
 * CMS-节点 Service
 *
 * @author tg
 * 2026-03-21 21:34:38
 */
public interface BizConfigService extends IService<BizConfig> {
    BizConfig getByCode(String code);
}
