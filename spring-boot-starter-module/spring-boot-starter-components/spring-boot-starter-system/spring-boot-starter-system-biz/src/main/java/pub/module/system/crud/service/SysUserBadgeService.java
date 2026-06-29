package pub.module.system.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.system.crud.entity.SysUserBadge;

import java.util.List;

/**
 * 用户角标 Service
 */
public interface SysUserBadgeService extends IService<SysUserBadge> {

    SysUserBadge getByUserCodeAndBadgeKey(String userCode, String badgeKey);

    List<SysUserBadge> listByUserCode(String userCode);
}
