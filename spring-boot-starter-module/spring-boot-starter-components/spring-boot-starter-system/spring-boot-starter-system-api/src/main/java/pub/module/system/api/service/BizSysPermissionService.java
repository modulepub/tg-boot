package pub.module.system.api.service;


import pub.module.system.api.service.dto.PermissionDTO;

import java.util.List;

public interface BizSysPermissionService {
    PermissionDTO getByCode(String perCode, List<String> perCodes);
    PermissionDTO getByCode(String perCode);
}
