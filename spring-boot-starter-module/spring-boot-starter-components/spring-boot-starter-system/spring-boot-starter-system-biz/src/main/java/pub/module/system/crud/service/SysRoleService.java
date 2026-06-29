package pub.module.system.crud.service;

import pub.module.system.crud.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 角色表 Service
 *
 * @author tg
 * 2026-01-04 13:16:23
 */
public interface SysRoleService extends IService<SysRole> {
    SysRole getByCode(String code);
}
