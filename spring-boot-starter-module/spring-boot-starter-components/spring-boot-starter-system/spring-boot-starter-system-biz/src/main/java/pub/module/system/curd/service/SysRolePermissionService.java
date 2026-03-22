package pub.module.system.curd.service;

import pub.module.system.curd.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 角色权限表 Service
 *
 * @author tg
 * 2026-01-04 13:16:24
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {
    SysRolePermission getByCode(String code);
}
