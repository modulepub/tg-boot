package pub.module.system.api.service;

/**
 * 管理端-用户账号注销申请业务接口
 */
public interface ApiSysUserCancellationMgtService {

    /**
     * 标记注销申请为已处理，并执行账号注销
     */
    void process(String id, String processBy);
}
