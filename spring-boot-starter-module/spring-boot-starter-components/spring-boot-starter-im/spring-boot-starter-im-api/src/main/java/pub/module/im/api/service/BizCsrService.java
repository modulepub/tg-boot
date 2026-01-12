package pub.module.im.api.service;


import pub.module.system.api.service.dto.UserDTO;

public interface BizCsrService {
    /**
     * 可以配置缓存
     */
    void initSysUser(UserDTO sysUser);
}
