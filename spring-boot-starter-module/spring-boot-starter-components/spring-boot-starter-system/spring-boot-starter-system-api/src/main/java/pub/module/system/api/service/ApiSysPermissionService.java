package pub.module.system.api.service;


import pub.module.system.api.service.dto.PermissionDTO;

import java.util.List;

public interface ApiSysPermissionService {
    List<PermissionDTO> getPermissionsByUserCode(String userCode);
    List<PermissionDTO> getPermissionsByUserName(String userName);
    List<PermissionDTO> getPermissions() ;
    PermissionDTO buildTree(String perCode, List<PermissionDTO> allPermissions);
}
