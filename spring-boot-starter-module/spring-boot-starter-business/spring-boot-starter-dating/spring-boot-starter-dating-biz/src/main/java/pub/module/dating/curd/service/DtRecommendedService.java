package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtRecommended;
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
