package pub.module.system.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.system.crud.entity.SysUserCancellationApply;

/**
 * 用户账号注销申请 Service
 */
public interface SysUserCancellationApplyService extends IService<SysUserCancellationApply> {

    SysUserCancellationApply getByCode(String code);
}
