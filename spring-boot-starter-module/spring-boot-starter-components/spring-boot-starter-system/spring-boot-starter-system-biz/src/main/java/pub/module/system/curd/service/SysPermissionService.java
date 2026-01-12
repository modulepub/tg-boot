package pub.module.system.curd.service;

import pub.module.system.curd.entity.SysPermission;
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
