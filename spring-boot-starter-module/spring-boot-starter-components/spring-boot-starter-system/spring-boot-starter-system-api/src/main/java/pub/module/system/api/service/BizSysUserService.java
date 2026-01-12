package pub.module.system.api.service;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.system.api.service.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface BizSysUserService {

    // 新用户添加

    @Data
    class LoginDTO{
        @TableField(exist = false)
        @Schema(description= "令牌")
        private java.lang.String accessToken;
        @Schema(description= "过期时间")
        Long expireTime;
    }

    void authSmsCode(String phone,String smsCode);
    void authUserNamePassword(String username, String password);
    void changePassword(String username, String oldPassword,String newPassword);
    UserDTO registerByPhone(String phone);

    UserDTO registerByOpenId(String openId);

    LoginDTO loginByCode(String userCode);


    LoginDTO changeOrg(String userCode,String orgCode);

    void logout(String userCode);

    void deleteByCode( String userCode);

    void sendSmsCode(String phone);

    void addSysUser(UserDTO sysUser);

    UserDTO getUserByUserCode(String userCode);

    UserDTO getUserByUserName(String sysUserName);

    void updateById(UserDTO userModel);

    List<UserDTO> list(UserDTO userDTO);

    IPage<UserDTO> page(UserDTO userDTO, long pageNo, long pageSize);

}
