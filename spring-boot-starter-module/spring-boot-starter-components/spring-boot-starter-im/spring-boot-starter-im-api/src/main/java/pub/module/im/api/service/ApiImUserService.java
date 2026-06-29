package pub.module.im.api.service;

import java.util.List;

/**
 * 管理端 IM 用户同步与资料拉取
 */
public interface ApiImUserService {

    /**
     * 从系统用户拉取资料并同步本地 im_user
     */
    void refreshFromSysUser(String userCode);

    /**
     * 批量将系统用户同步为 IM 用户
     *
     * @return 成功数量
     */
    int batchSyncFromSysUsers(List<String> userCodes);

    /**
     * 刷新全部已存在的 IM 用户资料（从系统用户拉取）
     *
     * @return 成功数量
     */
    int refreshAllFromSysUsers();
}
