package pub.module.system.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pub.module.system.api.constants.SysUserEnum;
import pub.module.system.api.service.ApiSysPermissionService;
import pub.module.system.api.service.ApiSysUserOrganizationService;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.crud.entity.SysPermission;
import pub.module.system.crud.entity.SysUser;
import pub.module.system.crud.service.SysPermissionService;

import jakarta.annotation.Resource;
import pub.module.system.crud.service.SysUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限业务 Service 实现
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Slf4j
@Service
public class ApiSysPermissionServiceImpl implements ApiSysPermissionService {
    @Resource
    SysPermissionService tgPermissionService;
    @Resource
    SysUserService userService;
    @Resource
    ApiSysUserOrganizationService apiSysUserOrganizationService;

    @Override
    public PermissionDTO buildTree(String perCode, List<PermissionDTO> allPermissions) {
        Assert.notNull(perCode, "perCode 不能为空");
        PermissionDTO top = null;
        List<PermissionDTO> allPermissionDTOs = BeanUtil.copyToList(allPermissions, PermissionDTO.class);
        // 构建权限编码到DTO的映射，方便快速查找
        Map<String, PermissionDTO> permissionMap = new HashMap<>();
        for (PermissionDTO dto : allPermissionDTOs) {
            permissionMap.put(dto.getPerCode(), dto);
        }
        // 组装树状结构
        for (PermissionDTO dto : allPermissionDTOs) {
            if (dto.getPerCode().equals(perCode)) {
                top = dto;
            }
            String parentCode = dto.getPerParentCode();
            // 如果找到父节点，并且父节点在权限列表中
            if (parentCode != null && permissionMap.containsKey(parentCode)) {
                PermissionDTO parentDto = permissionMap.get(parentCode);
                if (parentDto.getChildren() == null) {
                    parentDto.setChildren(new ArrayList<>());
                }
                parentDto.getChildren().add(dto);
            }
        }
        return top;
    }

    @Override
    public List<PermissionDTO> getPermissionsByUserCode(String userCode) {
        List<PermissionDTO> result  = new ArrayList<>();
        apiSysUserOrganizationService.ensureUserOrgCode(userCode);
        SysUser sysUser = userService.getByCode(userCode);
        String userOrgCode = sysUser.getUserOrgCode();
        if(StrUtil.isNotBlank(userOrgCode)){
            QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
            if(!SysUserEnum.SUPPER_USER.getCode().equals(userCode)){
                String sql = buildRolePermissionSql(userOrgCode, userCode);
                queryWrapper.lambda().inSql(SysPermission::getPerCode, sql);
            }
            queryWrapper.lambda().orderByAsc(SysPermission::getSeqNo);
            List<PermissionDTO> list = BeanUtil.copyToList(tgPermissionService.list(queryWrapper), PermissionDTO.class);
            result.addAll(list);
        }

        return result;
    }

    @Override
    public List<PermissionDTO> getPermissionsByUserName(String userName) {
        List<PermissionDTO> result = new ArrayList<>();
        SysUser sysUser = userService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName,userName),false);
        if (sysUser == null) {
            return result;
        }
        apiSysUserOrganizationService.ensureUserOrgCode(sysUser.getUserCode());
        sysUser = userService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName,userName),false);
        String userOrgCode = sysUser.getUserOrgCode();
        if (StrUtil.isNotBlank(userOrgCode)) {
            QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
            if (!SysUserEnum.SUPPER_USER.getCode().equals(sysUser.getUserCode())) {
                String sql = buildRolePermissionSql(userOrgCode, sysUser.getUserCode());
                queryWrapper.lambda().inSql(SysPermission::getPerCode, sql);
            }
            queryWrapper.lambda().orderByAsc(SysPermission::getSeqNo);
            List<PermissionDTO> list = BeanUtil.copyToList(tgPermissionService.list(queryWrapper), PermissionDTO.class);
            result.addAll(list);
        }

        return result;
    }

    @Override
    public List<PermissionDTO> getPermissions() {
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(SysPermission::getSeqNo);
        return BeanUtil.copyToList(tgPermissionService.list(queryWrapper), PermissionDTO.class);
    }

    /**
     * 按当前登录部门 user_org_code 匹配机构角色。
     */
    private String buildRolePermissionSql(String userOrgCode, String userCode) {
        return "select per_code from sys_role_permission where deleted = 0 and role_code in ("
                + "select role_code from sys_user_organization where deleted = 0 and user_code = '"
                + userCode
                + "' and org_code = '"
                + userOrgCode
                + "'"
                + ")";
    }


}
