package pub.module.system.api.event;

import pub.module.system.api.service.dto.UserDTO;

/**
 * 系统用户登录成功事件（在签发 token、写入登录缓存之后由业务层发布）。
 */
public class SysUserLoginEvent {

    private final UserDTO user;

    public SysUserLoginEvent(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }
}
