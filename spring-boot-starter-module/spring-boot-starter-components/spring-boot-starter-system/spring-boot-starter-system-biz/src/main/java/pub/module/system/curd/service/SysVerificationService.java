package pub.module.system.curd.service;

import pub.module.system.curd.entity.SysVerification;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 验证码 Service
 *
 * @author tg
 * 2026-04-20 14:14:27
 */
public interface SysVerificationService extends IService<SysVerification> {
    SysVerification getByCode(String code);
}
