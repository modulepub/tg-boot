package pub.module.system.api.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.module.system.api.service.dto.UserDTO;

import java.io.Serializable;

/**
 * 新用户注册成功消息（供分销等模块绑定推荐关系）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRegisteredMessage implements Serializable {

    private UserDTO user;

    /** 注册时填写的推荐人 userCode（可为空） */
    private String userReferenceUserCode;
}
