package pub.module.im.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.im.crud.entity.ImNotice;

public interface ImNoticeService extends IService<ImNotice> {

    ImNotice getByCode(String imNoticeCode);

    void markPublished(String id, int successCount, int failCount);
}
