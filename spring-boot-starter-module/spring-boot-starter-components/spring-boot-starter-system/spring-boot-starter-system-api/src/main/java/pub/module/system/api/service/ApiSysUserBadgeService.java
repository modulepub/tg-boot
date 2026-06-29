package pub.module.system.api.service;

import pub.module.system.api.service.dto.SysUserBadgeDTO;

import java.util.List;

/**
 * 用户角标业务接口（供各模块调用）
 */
public interface ApiSysUserBadgeService {

    /**
     * 查询用户全部角标
     */
    List<SysUserBadgeDTO> listByUserCode(String userCode);

    /**
     * 清零指定角标
     */
    void clearBadge(String userCode, String badgeKey);

    /**
     * 设置角标数量（小于 0 时按 0 处理）
     */
    void setBadgeCount(String userCode, String badgeKey, int badgeCount);

    /**
     * 累加角标数量（结果小于 0 时按 0 处理）
     */
    void incrementBadgeCount(String userCode, String badgeKey, int delta);
}
