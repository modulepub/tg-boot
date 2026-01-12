package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtLike;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 喜欢 Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
public interface DtLikeService extends IService<DtLike> {
    DtLike getByCode(String code);
}
