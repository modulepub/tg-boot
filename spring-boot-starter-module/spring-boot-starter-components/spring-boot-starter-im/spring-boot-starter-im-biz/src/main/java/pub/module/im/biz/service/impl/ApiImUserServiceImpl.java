package pub.module.im.biz.service.impl;

import pub.module.common.enums.StatusCodeEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.ApiImUserService;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.im.crud.entity.ImUser;
import pub.module.im.crud.service.ImUserService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.util.List;

@Slf4j
@Service
public class ApiImUserServiceImpl implements ApiImUserService {

    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private ApiImService apiImService;
    @Resource
    private ImUserService imUserService;

    @Override
    public void refreshFromSysUser(String userCode) {
        Assert.notBlank(userCode, "userCode is null");
        UserDTO sysUser = apiSysUserService.getUserByUserCode(userCode.trim());
        Assert.notNull(sysUser, "系统用户不存在: " + userCode);
        ImAccountDTO account = toImAccount(sysUser);
        apiImService.saveOrUpdateAccount(account);
        apiImService.generateUserSig(account.getUserCode());
        apiSysUserService.updateUserImSynStatusByUserCode(account.getUserCode(), StatusCodeEnum.YES);
    }

    @Override
    public int batchSyncFromSysUsers(List<String> userCodes) {
        if (CollUtil.isEmpty(userCodes)) {
            return 0;
        }
        int success = 0;
        for (String userCode : userCodes) {
            if (StrUtil.isBlank(userCode)) {
                continue;
            }
            try {
                refreshFromSysUser(userCode.trim());
                success++;
            } catch (Exception e) {
                log.warn("同步 IM 用户失败, userCode={}", userCode, e);
            }
        }
        Assert.isTrue(success > 0, "同步失败，请检查所选用户是否存在且资料完整");
        return success;
    }

    @Override
    public int refreshAllFromSysUsers() {
        List<ImUser> users = imUserService.list(new QueryWrapper<ImUser>()
                .select("im_user_user_code")
                .eq("deleted", 0)
                .isNotNull("im_user_user_code"));
        if (CollUtil.isEmpty(users)) {
            return 0;
        }
        int success = 0;
        for (ImUser user : users) {
            String userCode = user.getImUserUserCode();
            if (StrUtil.isBlank(userCode)) {
                continue;
            }
            try {
                refreshFromSysUser(userCode.trim());
                success++;
            } catch (Exception e) {
                log.warn("刷新 IM 用户失败, userCode={}", userCode, e);
            }
        }
        Assert.isTrue(success > 0, "刷新失败，请检查 IM 用户对应的系统用户是否存在且资料完整");
        return success;
    }

    private ImAccountDTO toImAccount(UserDTO sysUser) {
        ImAccountDTO account = new ImAccountDTO();
        account.setUserCode(sysUser.getUserCode());
        account.setNickName(sysUser.getUserNickName());
        account.setAvatar(sysUser.getUserAvatar());
        account.setRealName(sysUser.getUserRealName());
        return account;
    }
}
