package pub.module.system.api.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.module.system.api.service.dto.UserDTO;

import java.io.Serializable;

/**
 * 系统用户信息更新消息（供客户等模块同步扩展字段）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserInfoUpdatedMessage implements Serializable {

    private UserDTO user;
}
