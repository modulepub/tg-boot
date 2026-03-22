package pub.module.system.api.service;

import java.util.List;

/**
 * 用户所属角色表 Service
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
public interface ApiSysUserOrganizationRoleService {
    List<String> getRoleCodesByOrgCodeAndUserCode(String orgCode, String userCode);
}
