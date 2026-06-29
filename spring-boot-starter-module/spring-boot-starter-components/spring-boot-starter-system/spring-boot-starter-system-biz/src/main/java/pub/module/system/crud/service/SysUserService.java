package pub.module.system.crud.service;

import pub.module.system.crud.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户表 Service
 *
 * @author tg
 * 2026-01-04 13:16:24
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getByCode(String code);
}
