package pub.module.system.api.service;

import pub.module.system.api.service.dto.SysUserCancellationApplyDTO;

/**
 * 用户账号注销申请业务接口
 */
public interface ApiSysUserCancellationService {

    /**
     * 用户提交注销申请
     */
    SysUserCancellationApplyDTO submitApply(String userCode);

    /**
     * 查询用户最新一条注销申请
     */
    SysUserCancellationApplyDTO getLatestByUserCode(String userCode);
}