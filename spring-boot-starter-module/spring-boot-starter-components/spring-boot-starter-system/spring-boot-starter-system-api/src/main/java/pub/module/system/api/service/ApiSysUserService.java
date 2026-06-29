package pub.module.system.api.service;

import pub.module.common.enums.StatusCodeEnum;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.vo.BindPhoneResultVO;

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

    void authSmsCode(String phone,String smsCode);
    void authUserNamePassword(String username, String password);
    void changePassword(String username, String oldPassword,String newPassword);

    /**
     * 管理端重置用户密码，返回明文新密码（8 位随机字符）
     */
    String resetPasswordById(String id);
    UserDTO registerByPhone(String phone, String userReferenceUserCode);

    /**
     * 根据微信 openId 查找或注册用户（与手机号登录注册逻辑一致，仅登录方式不同）。
     *
     * @param openId                微信 openId
     * @param userReferenceUserCode 分享人用户编码（首次登录注册时传入）
     */
    UserDTO registerByOpenId(String openId, String userReferenceUserCode);

    /**
     * 绑定微信小程序 openId 到当前用户。
     */
    void bindWxMaOpenId(String userCode, String openId);

    /**
     * 校验短信验证码后为当前用户绑定手机号。
     * 若手机号已被其他账号占用且 {@code confirmSwitch=false}，抛出 {@link pub.module.system.api.constants.SysErrorCodeEnum#PHONE_ALREADY_EXISTS}；
     * 若 {@code confirmSwitch=true}，按当前 userCode 读取 openId，解绑后绑定到该手机号账号并返回新登录 token。
     */
    BindPhoneResultVO bindPhone(String currentUserCode, String phone, String smsAuthCode, boolean confirmSwitch);

    LoginDTO changeOrg(String userCode,String orgCode);

    void logoutByUserName(String userName);
    void logoutByCode(String userCode);

    /** 注销指定 Token 会话（按 userCode + jti） */
    void logoutBySession(String userCode, String jti);

    void deleteByCode( String userCode);

    void sendSmsCode(String phone);

    void addSysUser(UserDTO sysUser);

    UserDTO getUserByUserCode(String userCode);

    UserDTO getUserByUserName(String sysUserName);

    void updateById(UserDTO userModel);
    void updateAvatarByUserCode(String  userCode,String userAvatar);

    /**
     * 按用户编码更新真实姓名（客户 cusName / 红娘 mkName 同步入口）
     */
    void updateUserRealNameByUserCode(String userCode, String userRealName);

    /**
     * 按用户编码更新昵称（客户 cusNickName 同步入口）
     */
    void updateUserNickNameByUserCode(String userCode, String userNickName);

    /**
     * 按用户编码更新实名认证状态
     */
    void updateUserIdentityAuthenticatedStatusByUserCode(String userCode, StatusCodeEnum status);

    void updateUserImSynStatusByUserCode(String userCode, StatusCodeEnum status);

    IPage<UserDTO> pageImUnsynced(String keyword, long pageNo, long pageSize);

    List<UserDTO> list(UserDTO userDTO);

    IPage<UserDTO> page(UserDTO userDTO, long pageNo, long pageSize);

    /**
     * 逻辑删除全部测试用户（{@code user_test_status_code = 1}）。
     *
     * @return 删除条数
     */
    int removeTestUsers();

    /**
     * 当前用户 {@code user_reference_user_code} 为空时，设置为指定推荐人用户编码；已有值不覆盖。
     *
     * @return 是否本次写入成功
     */
    boolean setReferenceUserCodeIfAbsent(String userCode, String referenceUserCode);

    /**
     * 为用户新增一个用户标签（按 userCode + tagCode 去重，已存在则忽略）。
     */
    void addUserTag(String userCode, String tagCode, String tagName);

}
