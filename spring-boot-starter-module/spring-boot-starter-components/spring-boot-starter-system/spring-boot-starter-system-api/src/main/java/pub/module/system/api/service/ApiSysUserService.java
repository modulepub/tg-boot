package pub.module.system.api.service;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.system.api.service.dto.UserDTO;

import java.util.List;

public interface ApiSysUserService {

    // 新用户添加

    @Data
    class LoginDTO{
        String userCode;
        @TableField(exist = false)
        @Schema(description= "令牌")
        private java.lang.String accessToken;
        @Schema(description= "过期时间")
        Long expireTime;
        String[] authorities;
    }


    ApiSysUserService.LoginDTO loginByCode(String userCode);

    void authenticate(String userCode);

    void authSmsCode(String phone,String smsCode);
    void authUserNamePassword(String username, String password);
    void changePassword(String username, String oldPassword,String newPassword);
    UserDTO registerByPhone(String phone, String userReferenceUserCode);

    UserDTO registerByOpenId(String openId);



    LoginDTO changeOrg(String userCode,String orgCode);

    void logoutByUserName(String userName);
    void logoutByCode(String userCode);

    void deleteByCode( String userCode);

    void sendSmsCode(String phone);

    void addSysUser(UserDTO sysUser);

    UserDTO getUserByUserCode(String userCode);

    UserDTO getUserByUserName(String sysUserName);

    void updateById(UserDTO userModel);
    void updateAvatarByUserCode(String  userCode,String userAvatar);

    List<UserDTO> list(UserDTO userDTO);

    IPage<UserDTO> page(UserDTO userDTO, long pageNo, long pageSize);

}
