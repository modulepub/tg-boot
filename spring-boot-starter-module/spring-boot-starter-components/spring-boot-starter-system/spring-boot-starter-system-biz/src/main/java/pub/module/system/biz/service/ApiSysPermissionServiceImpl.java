package pub.module.system.biz.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pub.module.system.api.constants.SysUserEnum;
import pub.module.system.api.service.ApiSysPermissionService;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.curd.entity.SysPermission;
import pub.module.system.curd.entity.SysUser;
import pub.module.system.curd.service.SysPermissionService;

import jakarta.annotation.Resource;
import pub.module.system.curd.service.SysUserService;

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
        SysUser sysUser = userService.getByCode(userCode);
        String userOrgCode = sysUser.getUserOrgCode();
        if(StrUtil.isNotEmpty(userOrgCode)){
            QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
            if(!SysUserEnum.SUPPER_USER.getCode().equals(userCode)){
                String sql = "select per_code from sys_role_permission where  deleted= 0 and role_code in (select role_code from sys_user_organization_role where deleted= 0 and  org_code = '${orgCode}' and user_code = '${userCode}')"
                        .replace("${orgCode}", userOrgCode)
                        .replace("${userCode}", userCode);
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


}
