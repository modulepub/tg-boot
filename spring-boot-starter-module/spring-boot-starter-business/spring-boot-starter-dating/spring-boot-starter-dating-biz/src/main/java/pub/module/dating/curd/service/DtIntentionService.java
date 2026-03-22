package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtIntention;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 交友意向 Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
public interface DtIntentionService extends IService<DtIntention> {
    DtIntention getByCode(String code);
}
