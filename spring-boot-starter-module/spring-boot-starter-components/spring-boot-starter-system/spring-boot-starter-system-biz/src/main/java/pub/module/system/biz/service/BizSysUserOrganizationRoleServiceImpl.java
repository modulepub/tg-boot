package pub.module.system.biz.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.system.api.service.BizSysUserOrganizationRoleService;
import pub.module.system.curd.entity.SysUserOrganizationRole;
import pub.module.system.curd.service.SysUserOrganizationRoleService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BizSysUserOrganizationRoleServiceImpl implements BizSysUserOrganizationRoleService {
    @Resource
    SysUserOrganizationRoleService sysUserOrganizationRoleService;
    public List<String> getRolesByUserCode(String userCode){
        Assert.notEmpty(userCode,"userCode is not null");
        List<SysUserOrganizationRole> sysUserOrganizationRoles = sysUserOrganizationRoleService.list(new QueryWrapper<SysUserOrganizationRole>().lambda().eq(SysUserOrganizationRole::getUserCode,userCode));
        return new ArrayList<>(sysUserOrganizationRoles.stream().map(SysUserOrganizationRole::getUserCode).toList());
    }



    @Override
    public List<String> getRolesByOrgCodeAndUserCode(String orgCode,String userCode) {
        List<SysUserOrganizationRole> sysUserOrganizationRoles = sysUserOrganizationRoleService.list(
                new QueryWrapper<SysUserOrganizationRole>().lambda()
                        .eq(SysUserOrganizationRole::getUserCode,userCode)
                        .eq(SysUserOrganizationRole::getOrgCode,orgCode)
        );
        return new ArrayList<>(sysUserOrganizationRoles.stream().map(SysUserOrganizationRole::getUserCode).filter(StrUtil::isNotEmpty).toList());
    }
}
