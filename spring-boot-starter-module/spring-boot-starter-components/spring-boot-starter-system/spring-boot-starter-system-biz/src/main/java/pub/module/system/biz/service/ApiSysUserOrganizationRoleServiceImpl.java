package pub.module.system.biz.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.system.api.service.ApiSysUserOrganizationRoleService;
import pub.module.system.curd.entity.SysUserOrganizationRole;
import pub.module.system.curd.service.SysUserOrganizationRoleService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApiSysUserOrganizationRoleServiceImpl implements ApiSysUserOrganizationRoleService {
    @Resource
    SysUserOrganizationRoleService sysUserOrganizationRoleService;

    @Override
    public List<String> getRoleCodesByOrgCodeAndUserCode(String orgCode,String userCode) {
        List<SysUserOrganizationRole> sysUserOrganizationRoles = sysUserOrganizationRoleService.list(
                new QueryWrapper<SysUserOrganizationRole>().lambda()
                        .eq(SysUserOrganizationRole::getUserCode,userCode)
                        .eq(SysUserOrganizationRole::getOrgCode,orgCode)
        );
        return new ArrayList<>(sysUserOrganizationRoles.stream().map(SysUserOrganizationRole::getRoleCode).filter(StrUtil::isNotEmpty).toList());
    }
}
