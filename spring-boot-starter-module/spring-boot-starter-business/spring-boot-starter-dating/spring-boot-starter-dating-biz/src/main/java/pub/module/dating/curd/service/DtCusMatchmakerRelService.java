package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtCusMatchmakerRel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客户红娘关系 Service
 *
 * @author tg
 * 2026-03-25 00:36:20
 */
public interface DtCusMatchmakerRelService extends IService<DtCusMatchmakerRel> {
    DtCusMatchmakerRel getByCode(String code);
}
