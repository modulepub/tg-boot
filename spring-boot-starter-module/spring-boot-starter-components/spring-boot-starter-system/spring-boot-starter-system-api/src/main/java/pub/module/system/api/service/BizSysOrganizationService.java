package pub.module.system.api.service;

import pub.module.system.api.service.dto.OrganizationDTO;

import java.util.List;

/**
 * 组织机构 Service
 *
 * @author tg
 * 2025-12-28
 */
public interface BizSysOrganizationService {
    OrganizationDTO getByCode(String sysOrgCode);
    List<OrganizationDTO> listRootCompany();
    void setTree(OrganizationDTO organizationDTO);
}
