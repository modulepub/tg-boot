package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtRecommended;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 推荐 Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
public interface DtRecommendedService extends IService<DtRecommended> {
    DtRecommended getByCode(String code);
}
