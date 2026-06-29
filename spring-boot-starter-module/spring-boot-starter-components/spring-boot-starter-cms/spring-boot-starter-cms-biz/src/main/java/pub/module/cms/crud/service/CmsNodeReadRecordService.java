package pub.module.cms.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.cms.crud.entity.CmsNodeReadRecord;

public interface CmsNodeReadRecordService extends IService<CmsNodeReadRecord> {

    CmsNodeReadRecord getBySessionCode(String sessionCode);

    /**
     * 查询同一文章、同一 IP 在指定分钟内的最近一条阅读记录。
     */
    CmsNodeReadRecord getRecentByNodeCodeAndIp(String nodeCode, String clientIp, int withinMinutes);
}
