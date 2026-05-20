package pub.module.system.api.event;

import pub.module.system.api.service.dto.UserDTO;

/**
 * 系统用户信息更新成功事件（用户端编辑资料等场景发布后，供业务插件同步扩展数据）。
 */
public class SysUserInfoUpdatedEvent {

    private final UserDTO user;

    public SysUserInfoUpdatedEvent(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
        return user;
    }
}
