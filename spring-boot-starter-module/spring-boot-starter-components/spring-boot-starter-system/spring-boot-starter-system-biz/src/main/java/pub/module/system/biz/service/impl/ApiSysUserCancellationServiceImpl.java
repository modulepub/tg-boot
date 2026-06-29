package pub.module.system.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.constants.SysUserCancellationProcessCodeEnum;
import pub.module.system.api.service.ApiSysUserCancellationService;
import pub.module.system.api.service.dto.SysUserCancellationApplyDTO;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.entity.SysUserCancellationApply;
import pub.module.system.crud.service.SysUserCancellationApplyService;
import pub.module.system.crud.service.SysUserService;

/**
 * 用户端账号注销申请
 */
@Service
public class ApiSysUserCancellationServiceImpl implements ApiSysUserCancellationService {

    @Resource
    private SysUserCancellationApplyService sysUserCancellationApplyService;
    @Resource
    private SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserCancellationApplyDTO submitApply(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            throw new IllegalArgumentException("用户未登录");
        }
        String trimmedUserCode = userCode.trim();
        SysUser sysUser = sysUserService.getByCode(trimmedUserCode);
        if (sysUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        SysUserCancellationApply pending = sysUserCancellationApplyService.getOne(
                new QueryWrapper<SysUserCancellationApply>().lambda()
                        .eq(SysUserCancellationApply::getUserCode, trimmedUserCode)
                        .eq(SysUserCancellationApply::getCancellationProcessCode, SysUserCancellationProcessCodeEnum.PENDING)
                        .orderByDesc(SysUserCancellationApply::getCreateTime)
                        .last("LIMIT 1"),
                false);
        if (pending != null) {
            throw new IllegalArgumentException("您已提交注销申请，请耐心等待处理");
        }
        SysUserCancellationApply apply = new SysUserCancellationApply();
        apply.setUserCode(trimmedUserCode);
        apply.setUserPhone(sysUser.getUserPhone());
        apply.setUserNickName(sysUser.getUserNickName());
        apply.setCancellationProcessCode(SysUserCancellationProcessCodeEnum.PENDING);
        sysUserCancellationApplyService.save(apply);
        return toDto(apply);
    }

    @Override
    public SysUserCancellationApplyDTO getLatestByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return null;
        }
        SysUserCancellationApply apply = sysUserCancellationApplyService.getOne(
                new QueryWrapper<SysUserCancellationApply>().lambda()
                        .eq(SysUserCancellationApply::getUserCode, userCode.trim())
                        .orderByDesc(SysUserCancellationApply::getCreateTime)
                        .last("LIMIT 1"),
                false);
        return toDto(apply);
    }

    private static SysUserCancellationApplyDTO toDto(SysUserCancellationApply apply) {
        if (apply == null) {
            return null;
        }
        return BeanUtil.copyProperties(apply, SysUserCancellationApplyDTO.class);
    }
}
