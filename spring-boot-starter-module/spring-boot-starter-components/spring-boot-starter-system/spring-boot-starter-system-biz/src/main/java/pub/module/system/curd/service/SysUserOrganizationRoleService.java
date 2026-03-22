package pub.module.system.curd.service;

import pub.module.system.curd.entity.SysUserOrganizationRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户所属角色表 Service
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
public interface SysUserOrganizationRoleService extends IService<SysUserOrganizationRole> {
    SysUserOrganizationRole getByCode(String code);
}
