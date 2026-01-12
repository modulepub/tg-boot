package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtCusMatchmaker;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 我的红娘 Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
public interface DtCusMatchmakerService extends IService<DtCusMatchmaker> {
    DtCusMatchmaker getByCode(String code);
}
