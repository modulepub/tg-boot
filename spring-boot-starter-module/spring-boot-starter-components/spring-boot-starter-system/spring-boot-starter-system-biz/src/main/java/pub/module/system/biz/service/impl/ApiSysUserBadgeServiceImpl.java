package pub.module.system.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.constants.SysUserBadgeKeyEnum;
import pub.module.system.api.service.ApiSysUserBadgeService;
import pub.module.system.api.service.dto.SysUserBadgeDTO;
import pub.module.system.crud.entity.SysUserBadge;
import pub.module.system.crud.service.SysUserBadgeService;

import java.util.List;

/**
 * 用户角标业务实现
 */
@Service
public class ApiSysUserBadgeServiceImpl implements ApiSysUserBadgeService {

    @Resource
    private SysUserBadgeService sysUserBadgeService;

    @Override
    public List<SysUserBadgeDTO> listByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return List.of();
        }
        return sysUserBadgeService.listByUserCode(userCode.trim()).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearBadge(String userCode, String badgeKey) {
        setBadgeCount(userCode, badgeKey, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setBadgeCount(String userCode, String badgeKey, int badgeCount) {
        String trimmedUserCode = requireUserCode(userCode);
        String trimmedBadgeKey = requireBadgeKey(badgeKey);
        int safeCount = Math.max(0, badgeCount);

        SysUserBadge badge = sysUserBadgeService.getByUserCodeAndBadgeKey(trimmedUserCode, trimmedBadgeKey);
        if (badge == null) {
            if (safeCount == 0) {
                return;
            }
            badge = new SysUserBadge();
            badge.setUserCode(trimmedUserCode);
            badge.setBadgeKey(trimmedBadgeKey);
            badge.setBadgeCount(safeCount);
            sysUserBadgeService.save(badge);
            return;
        }
        badge.setBadgeCount(safeCount);
        sysUserBadgeService.updateById(badge);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementBadgeCount(String userCode, String badgeKey, int delta) {
        if (delta == 0) {
            return;
        }
        String trimmedUserCode = requireUserCode(userCode);
        String trimmedBadgeKey = requireBadgeKey(badgeKey);

        SysUserBadge badge = sysUserBadgeService.getByUserCodeAndBadgeKey(trimmedUserCode, trimmedBadgeKey);
        if (badge == null) {
            int initialCount = Math.max(0, delta);
            if (initialCount == 0) {
                return;
            }
            badge = new SysUserBadge();
            badge.setUserCode(trimmedUserCode);
            badge.setBadgeKey(trimmedBadgeKey);
            badge.setBadgeCount(initialCount);
            sysUserBadgeService.save(badge);
            return;
        }
        int nextCount = Math.max(0, (badge.getBadgeCount() == null ? 0 : badge.getBadgeCount()) + delta);
        badge.setBadgeCount(nextCount);
        sysUserBadgeService.updateById(badge);
    }

    private static String requireUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            throw new IllegalArgumentException("用户编码不能为空");
        }
        return userCode.trim();
    }

    private static String requireBadgeKey(String badgeKey) {
        if (StrUtil.isBlank(badgeKey)) {
            throw new IllegalArgumentException("角标 key 不能为空");
        }
        String trimmedBadgeKey = badgeKey.trim();
        if (SysUserBadgeKeyEnum.fromJson(trimmedBadgeKey) == null) {
            throw new IllegalArgumentException("角标 key 无效: " + trimmedBadgeKey);
        }
        return trimmedBadgeKey;
    }

    private SysUserBadgeDTO toDto(SysUserBadge badge) {
        return BeanUtil.copyProperties(badge, SysUserBadgeDTO.class);
    }
}
