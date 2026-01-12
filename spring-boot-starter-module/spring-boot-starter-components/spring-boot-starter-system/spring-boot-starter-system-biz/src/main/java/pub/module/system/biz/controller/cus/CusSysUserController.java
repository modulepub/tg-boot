package pub.module.system.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.vo.SysUserTokenVO;
import pub.module.system.curd.entity.SysUser;
import pub.module.system.curd.service.SysUserService;
import pub.module.web.vo.Result;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;
import java.io.Serializable;


/**
 * 系统用户 Controller
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name = "系统用户")
@RestController
@RequestMapping("/cus/sysUser")
@Slf4j
public class CusSysUserController {
   @Resource
   private BizSysUserService bizSysUserService;
   @Resource
   private SysUserService sysUserService;

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping(value = "/getInfo")
    public Result<UserDTO> getLoginUser() {
        UserDTO loginUser = UserUtil.getCurrentSysUser();
        return Result.ok(loginUser);
    }

    @Data
    public static  class  EditUserInfoVO{
        String userRealName;
        String userPhone;
        String userSexCode;
        String userAvatar;
    }
    @Operation(summary = "编辑用户信息")
    @PostMapping(value = "/editUserInfo")
    public Result<String> editUserInfo(@RequestBody EditUserInfoVO editUserInfoVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        SysUser sysUser = sysUserService.getByCode(userDTO.getUserCode());
        BeanUtil.copyProperties(editUserInfoVO, sysUser);
        sysUserService.updateById(sysUser);
        return Result.ok("编辑用户信息!");
    }

    @Data
    public static  class  ChangePasswordVO{
        String oldPassword;
        String newPassword;
    }
    @Operation(summary = "修改密码")
    @PostMapping(value = "/changePassword")
    public Result<String> changePassword(@RequestBody ChangePasswordVO changePasswordVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        bizSysUserService.changePassword(userDTO.getUserName(), changePasswordVO.getOldPassword(),changePasswordVO.getNewPassword());
        return Result.ok("修改密码成功!");
    }



    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "获取用户信息")
    public static class GetInfoByCodeVO implements Serializable {
        @Schema(description = "用户编码")
        private java.lang.String userCode;
    }
    @Operation(summary = "获取用户信息-通过用户编码")
    @GetMapping(value = "/getInfoByCode")
    public Result<UserDTO> getInfoByCode(GetInfoByCodeVO getInfoByCodeVO) {
        UserDTO result = bizSysUserService.getUserByUserCode(getInfoByCodeVO.getUserCode());
        return Result.ok(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出")
    public Result<String> logout() {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        bizSysUserService.logout(userDTO.getUserCode());
        return Result.ok();
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "获取 accessToken")
    public Result<SysUserTokenVO> token(String refreshToken) {
        BizSysUserService.LoginDTO loginDTO = bizSysUserService.loginByCode(UserUtil.getCurrentSysUser().getUserCode());
        SysUserTokenVO token = new SysUserTokenVO(loginDTO.getAccessToken(), loginDTO.getAccessToken(), loginDTO.getExpireTime(),  loginDTO.getExpireTime());
        return Result.ok(token);
    }
}
