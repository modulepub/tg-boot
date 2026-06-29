package pub.module.system.crud.service;

import pub.module.system.crud.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 菜单管理 Service
 *
 * @author tg
 * 2026-01-04 13:16:23
 */
public interface SysPermissionService extends IService<SysPermission> {
    SysPermission getByCode(String code);
}
