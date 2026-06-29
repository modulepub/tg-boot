package pub.module.dating.crud.service;

import pub.module.dating.crud.entity.DtMatchmakingCompany;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 红娘信息 Service
 *
 * @author tg
 * 2026-03-22 13:32:45
 */
public interface DtMatchmakingCompanyService extends IService<DtMatchmakingCompany> {
    DtMatchmakingCompany getByCode(String code);

    /** 按管理员用户编码查询其负责的企业（至多一条） */
    DtMatchmakingCompany getByAdminUserCode(String adminUserCode);
}
