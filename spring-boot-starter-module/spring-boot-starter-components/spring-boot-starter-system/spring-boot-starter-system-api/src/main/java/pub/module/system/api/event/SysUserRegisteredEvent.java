package pub.module.system.api.event;

import pub.module.system.api.service.dto.UserDTO;

/**
 * 新用户注册成功事件。
 */
public class SysUserRegisteredEvent {

    private final UserDTO user;
    private final String userReferenceUserCode;

    public SysUserRegisteredEvent(UserDTO user, String userReferenceUserCode) {
        this.user = user;
        this.userReferenceUserCode = userReferenceUserCode;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getUserReferenceUserCode() {
        return userReferenceUserCode;
    }
}
