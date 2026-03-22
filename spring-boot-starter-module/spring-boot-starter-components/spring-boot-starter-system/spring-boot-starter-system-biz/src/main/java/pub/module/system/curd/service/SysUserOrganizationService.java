package pub.module.system.curd.service;

import pub.module.system.curd.entity.SysUserOrganization;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户所属组织机构 Service
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
public interface SysUserOrganizationService extends IService<SysUserOrganization> {
    SysUserOrganization getByCode(String code);
}
