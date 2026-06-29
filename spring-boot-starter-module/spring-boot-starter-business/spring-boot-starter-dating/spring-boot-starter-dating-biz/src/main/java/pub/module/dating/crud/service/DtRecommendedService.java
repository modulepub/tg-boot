package pub.module.dating.crud.service;

import pub.module.dating.crud.entity.DtRecommended;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 对象推荐 Service
 *
 * @author tg
 * 2026-03-30 00:52:26
 */
public interface DtRecommendedService extends IService<DtRecommended> {
    DtRecommended getByCode(String code);
}
