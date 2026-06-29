package pub.module.system.crud.service;

import pub.module.system.crud.entity.SysOrganization;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 组织机构表 Service
 *
 * @author tg
 * 2026-01-04 13:16:22
 */
public interface SysOrganizationService extends IService<SysOrganization> {
    SysOrganization getByCode(String code);

    /** 机构编码为空时自动生成并落库，保证树选择可用 */
    void ensureOrgCode(SysOrganization organization);
}
