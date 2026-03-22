package pub.module.cms.curd.service;

import pub.module.cms.curd.entity.CmsNode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * CMS-节点 Service
 *
 * @author tg
 * 2026-03-08 16:04:07
 */
public interface CmsNodeService extends IService<CmsNode> {
    CmsNode getByCode(String code);
}
