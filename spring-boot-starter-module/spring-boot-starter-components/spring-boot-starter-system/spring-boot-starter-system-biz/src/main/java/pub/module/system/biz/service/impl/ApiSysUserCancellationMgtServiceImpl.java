package pub.module.system.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.constants.SysUserCancellationProcessCodeEnum;
import pub.module.system.api.constants.UserEnabledCodeEnum;
import pub.module.system.api.service.ApiSysUserCancellationMgtService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.entity.SysUserCancellationApply;
import pub.module.system.crud.service.SysUserCancellationApplyService;
import pub.module.system.crud.service.SysUserService;

import java.time.LocalDateTime;

/**
 * 管理端账号注销申请处理
 */
@Service
public class ApiSysUserCancellationMgtServiceImpl implements ApiSysUserCancellationMgtService {

    @Resource
    private SysUserCancellationApplyService sysUserCancellationApplyService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(String id, String processBy) {
        SysUserCancellationApply apply = requirePending(id);
        LocalDateTime now = LocalDateTime.now();
        apply.setCancellationProcessCode(SysUserCancellationProcessCodeEnum.PROCESSED);
        apply.setProcessBy(StrUtil.trim(processBy));
        apply.setProcessAt(now);
        sysUserCancellationApplyService.updateById(apply);

        String userCode = StrUtil.trim(apply.getUserCode());
        if (StrUtil.isBlank(userCode)) {
            return;
        }
        apiSysUserService.logoutByCode(userCode);
        SysUser sysUser = sysUserService.getByCode(userCode);
        if (sysUser != null) {
            sysUser.setUserEnabledCode(UserEnabledCodeEnum.DISABLED);
            sysUserService.updateById(sysUser);
        }
        apiSysUserService.deleteByCode(userCode);
    }

    private SysUserCancellationApply requirePending(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("申请 id 不能为空");
        }
        SysUserCancellationApply apply = sysUserCancellationApplyService.getById(id);
        if (apply == null) {
            throw new IllegalArgumentException("注销申请不存在");
        }
        SysUserCancellationProcessCodeEnum process = apply.getCancellationProcessCode();
        if (process != SysUserCancellationProcessCodeEnum.PENDING) {
            throw new IllegalArgumentException("仅待处理的申请可标记为已处理");
        }
        return apply;
    }
}
