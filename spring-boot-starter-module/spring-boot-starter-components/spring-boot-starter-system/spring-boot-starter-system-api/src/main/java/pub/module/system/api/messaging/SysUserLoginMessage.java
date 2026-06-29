package pub.module.system.api.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.module.system.api.service.dto.UserDTO;

import java.io.Serializable;

/**
 * 用户登录成功消息（供客户等模块幂等初始化档案）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserLoginMessage implements Serializable {

    private UserDTO user;
}
