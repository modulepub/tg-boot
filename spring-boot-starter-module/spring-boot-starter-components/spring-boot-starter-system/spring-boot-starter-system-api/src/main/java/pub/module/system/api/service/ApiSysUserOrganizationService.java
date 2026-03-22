package pub.module.system.api.service;


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
    List<String> getSysOrganizationNameByUserCode(String userCode);
    void saveOrgCodes(List<String> orgCodes, String userCode);
}
