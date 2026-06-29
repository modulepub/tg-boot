package pub.module.dating.crud.service;

import pub.module.dating.crud.entity.DtMatch;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 匹配申请（牵线） Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
public interface DtMatchService extends IService<DtMatch> {
    DtMatch getByCode(String code);
}
