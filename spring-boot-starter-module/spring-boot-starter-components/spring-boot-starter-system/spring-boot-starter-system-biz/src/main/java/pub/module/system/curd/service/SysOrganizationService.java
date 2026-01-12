package pub.module.system.curd.service;

import pub.module.system.curd.entity.SysOrganization;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 组织机构表 Service
 *
 * @author tg
 * 2026-01-04 13:16:22
 */
public interface SysOrganizationService extends IService<SysOrganization> {
    SysOrganization getByCode(String code);
}
