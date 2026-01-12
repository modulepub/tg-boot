package pub.module.system.api.service;

import java.util.List;

/**
 * 角色权限表 Service
 *
 * @author tg
 * 2026-01-04 13:16:24
 */
public interface BizSysRolePermissionService {
    List<String> getPermissionsByRoles(List<String> roleCodes);
}
