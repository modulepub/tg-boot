package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtMatchmakingCompany;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 红娘信息 Service
 *
 * @author tg
 * 2026-03-22 13:32:45
 */
public interface DtMatchmakingCompanyService extends IService<DtMatchmakingCompany> {
    DtMatchmakingCompany getByCode(String code);
}
