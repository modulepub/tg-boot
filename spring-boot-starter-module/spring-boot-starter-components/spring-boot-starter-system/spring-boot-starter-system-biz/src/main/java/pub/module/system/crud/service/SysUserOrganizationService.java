package pub.module.system.crud.service;

import pub.module.system.crud.entity.SysUserOrganization;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户所属组织机构 Service
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
public interface SysUserOrganizationService extends IService<SysUserOrganization> {
    SysUserOrganization getByCode(String code);
}
