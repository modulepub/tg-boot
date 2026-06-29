package pub.module.im.biz.controller.cus;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.im.api.service.dto.ImInitImUserDTO;
import pub.module.im.api.service.dto.ImSaveProfileDTO;
import pub.module.im.crud.entity.ImUser;
import pub.module.im.crud.service.ImUserService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

@Tag(name = "用户端-IM服务")
@RestController
@RequestMapping("/cus/im")
public class CusImController {

    @Resource
    private ApiImService apiImService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private ImUserService imUserService;

    @Operation(summary = "用户端-初始化IM用户")
    @PostMapping("/initImUser")
    public Result<String> initImUser(@RequestBody(required = false) ImInitImUserDTO request) {
        UserDTO currentUser = UserUtil.getCurrentSysUser();
        ImAccountDTO actualAccount = new ImAccountDTO();
        actualAccount.setUserCode(currentUser.getUserCode());
        actualAccount.setNickName(currentUser.getUserNickName());
        actualAccount.setAvatar(currentUser.getUserAvatar());
        actualAccount.setRealName(currentUser.getUserRealName());
        apiImService.saveOrUpdateAccount(actualAccount);
        if (request != null && StrUtil.isNotBlank(request.getTag())) {
            ImUser imUser = imUserService.getByUserCode(actualAccount.getUserCode());
            if (imUser != null) {
                imUser.setImUserTag(request.getTag().trim());
                imUserService.updateById(imUser);
            }
        }
        apiSysUserService.updateUserImSynStatusByUserCode(actualAccount.getUserCode(), StatusCodeEnum.YES);
        return Result.ok(apiImService.generateUserSig(actualAccount.getUserCode()));
    }

    @Operation(summary = "用户端-退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        UserDTO currentUser = UserUtil.getCurrentSysUser();
        apiImService.logoutByUserCode(currentUser.getUserCode());
        return Result.ok("退出成功");
    }

    @Operation(summary = "用户端-保存/更新当前用户 IM 资料（昵称、头像）")
    @PostMapping("/saveOrUpdateProfile")
    public Result<String> saveOrUpdateProfile(@RequestBody(required = false) ImSaveProfileDTO request) {
        UserDTO currentUser = UserUtil.getCurrentSysUser();
        ImAccountDTO account = new ImAccountDTO();
        account.setUserCode(currentUser.getUserCode());
        if (request != null) {
            if (StrUtil.isNotBlank(request.getNickName())) {
                account.setNickName(request.getNickName().trim());
            }
            if (StrUtil.isNotBlank(request.getAvatar())) {
                account.setAvatar(request.getAvatar().trim());
            }
            if (StrUtil.isNotBlank(request.getRealName())) {
                account.setRealName(request.getRealName().trim());
            }
        }
        if (StrUtil.isBlank(account.getNickName())) {
            account.setNickName(currentUser.getUserNickName());
        }
        if (StrUtil.isBlank(account.getAvatar())) {
            account.setAvatar(currentUser.getUserAvatar());
        }
        if (StrUtil.isBlank(account.getRealName())) {
            account.setRealName(currentUser.getUserRealName());
        }
        apiImService.saveOrUpdateAccount(account);
        apiSysUserService.updateUserImSynStatusByUserCode(account.getUserCode(), StatusCodeEnum.YES);
        return Result.ok("同步成功");
    }
}
