package pub.module.system.api.service;


import pub.module.system.api.service.dto.OrganizationDTO;
import pub.module.system.api.service.dto.UserOrgRoleDTO;

import java.util.List;

/**
 * 用户所属组织机构 Service
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
public interface ApiSysUserOrganizationService {
    List<String> getUserCodes(String orgCode);
    List<String> getOrgCodes(String userCode);

    List<OrganizationDTO> listOrganizationsByUserCode(String userCode);
    List<String> getSysOrganizationNameByUserCode(String userCode);

    /** 保存用户机构角色（全量覆盖） */
    void saveUserOrganizations(String userCode, List<UserOrgRoleDTO> items);

    List<String> getRoleCodesByOrgCodeAndUserCode(String orgCode, String userCode);

    /** 按关联序号取用户第一个有效机构编码，无机构时返回 null */
    String getFirstOrgCode(String userCode);

    /** 用户可切换的部门编码（来自所属机构关联，不含当前登录部门逻辑） */
    List<String> getValidOrgCodes(String userCode);

    /**
     * 登录时若 user_org_code 为空，取所属机构列表第一个写入；已有值则保持不变。
     */
    String ensureUserOrgCode(String userCode);
}
