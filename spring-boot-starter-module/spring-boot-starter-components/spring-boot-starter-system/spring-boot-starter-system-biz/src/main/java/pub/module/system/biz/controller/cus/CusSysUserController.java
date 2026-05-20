package pub.module.system.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import cn.hutool.core.util.StrUtil;
import pub.module.system.api.event.SysUserInfoUpdatedEvent;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.vo.SysUserTokenVO;
import pub.module.system.curd.entity.SysUser;
import pub.module.system.curd.service.SysUserService;
import pub.module.common.model.vo.Result;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;

import java.io.Serializable;


/**
 * 用户端-系统用户
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Tag(name = "用户端-用户")
@RestController
@RequestMapping("/cus/sysUser")
@Slf4j
public class CusSysUserController {
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Operation(summary = "用户端-获取当前登录用户信息")
    @GetMapping(value = "/getInfo")
    public Result<UserDTO> getLoginUser() {
        UserDTO loginUser = UserUtil.getCurrentSysUser();
        return Result.ok(loginUser);
    }

    @Data
    public static class EditUserInfoVO {
        String userRealName;
        String userPhone;
        String userSexCode;
        String userAvatar;
        /**
         * 用户昵称
         */
        @Schema(description = "用户端-用户昵称")
        private String userNickName;

    }

    @Operation(summary = "用户端-编辑用户信息")
    @PostMapping(value = "/editUserInfo")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> editUserInfo(@RequestBody EditUserInfoVO editUserInfoVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        SysUser sysUser = sysUserService.getByCode(userDTO.getUserCode());
        if (editUserInfoVO.getUserAvatar() != null) {
            sysUser.setUserAvatar(editUserInfoVO.getUserAvatar());
        }
        boolean nickTouched = editUserInfoVO.getUserNickName() != null;
        if (nickTouched) {
            String nick = StrUtil.trim(editUserInfoVO.getUserNickName());
            if (StrUtil.isBlank(nick)) {
                return Result.error("昵称不能为空");
            }
            SysUser duplicate = sysUserService.lambdaQuery()
                    .eq(SysUser::getUserNickName, nick)
                    .ne(SysUser::getUserCode, userDTO.getUserCode())
                    .last("LIMIT 1")
                    .one();
            if (duplicate != null) {
                return Result.error("该昵称已被使用，请换一个");
            }
            sysUser.setUserNickName(nick);
        }
        sysUserService.updateById(sysUser);
        if (nickTouched && StrUtil.isNotBlank(userDTO.getUserCode())) {
            applicationEventPublisher.publishEvent(new SysUserInfoUpdatedEvent(
                    new UserDTO()
                            .setUserCode(userDTO.getUserCode())
                            .setUserNickName(sysUser.getUserNickName())));
        }
        return Result.ok("编辑用户信息!");
    }

    @Data
    public static class ChangePasswordVO {
        String oldPassword;
        String newPassword;
    }

    @Operation(summary = "用户端-修改密码")
    @PostMapping(value = "/changePassword")
    public Result<String> changePassword(@RequestBody ChangePasswordVO changePasswordVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        apiSysUserService.changePassword(userDTO.getUserName(), changePasswordVO.getOldPassword(), changePasswordVO.getNewPassword());
        return Result.ok("修改密码成功!");
    }


    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "用户端-获取用户信息")
    public static class GetInfoByCodeVO implements Serializable {
        @Schema(description = "用户端-用户编码")
        private java.lang.String userCode;
    }

    @Operation(summary = "用户端-获取用户信息-通过用户编码")
    @GetMapping(value = "/getInfoByCode")
    public Result<UserDTO> getInfoByCode(GetInfoByCodeVO getInfoByCodeVO) {
        UserDTO result = apiSysUserService.getUserByUserCode(getInfoByCodeVO.getUserCode());
        return Result.ok(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户端-退出")
    public Result<String> logout() {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        apiSysUserService.logoutByCode(userDTO.getUserCode());
        return Result.ok();
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "用户端-获取accessToken")
    public Result<SysUserTokenVO> token(String refreshToken) {
        ApiSysUserService.LoginDTO loginDTO = apiSysUserService.loginByCode(UserUtil.getCurrentSysUser().getUserCode());
        SysUserTokenVO token = new SysUserTokenVO(loginDTO.getAccessToken(), loginDTO.getAccessToken(), loginDTO.getExpireTime(), loginDTO.getExpireTime());
        return Result.ok(token);
    }
}
