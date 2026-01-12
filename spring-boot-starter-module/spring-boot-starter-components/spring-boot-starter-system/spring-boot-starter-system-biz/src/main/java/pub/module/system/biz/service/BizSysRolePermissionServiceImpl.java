package pub.module.system.biz.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.system.api.service.BizSysRolePermissionService;
import pub.module.system.curd.entity.SysRolePermission;
import pub.module.system.curd.service.SysRolePermissionService;

import java.util.List;

@Service
public class BizSysRolePermissionServiceImpl implements BizSysRolePermissionService {
    @Resource
    SysRolePermissionService sysRolePermissionService;
    @Override
    public List<String> getPermissionsByRoles(List<String> roleCodes) {
        // Queries permissions for given roles; filters nonempty results
        return new java.util.ArrayList<>(sysRolePermissionService.list(new QueryWrapper<SysRolePermission>()
                .lambda().in(SysRolePermission::getRoleCode, roleCodes)).stream().map(SysRolePermission::getPerCode).filter(StrUtil::isNotEmpty).toList());
    }
}
