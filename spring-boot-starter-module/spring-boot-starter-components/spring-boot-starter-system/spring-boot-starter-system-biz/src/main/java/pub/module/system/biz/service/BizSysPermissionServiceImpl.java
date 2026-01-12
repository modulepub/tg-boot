package pub.module.system.biz.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pub.module.system.api.service.BizSysPermissionService;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.curd.entity.SysPermission;
import pub.module.system.curd.service.SysPermissionService;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 权限业务 Service 实现
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Service
public class BizSysPermissionServiceImpl implements BizSysPermissionService {
    @Resource
    SysPermissionService tgPermissionService;

    public void setTree(PermissionDTO sysPermission, List<SysPermission> allPermissions) {
        Assert.notNull(sysPermission, "TgPermission 不能为空");
        List<PermissionDTO> allPermissionDTOs = BeanUtil.copyToList(allPermissions, PermissionDTO.class);
        // 构建权限编码到DTO的映射，方便快速查找
        java.util.Map<String, PermissionDTO> permissionMap = new java.util.HashMap<>();
        for (PermissionDTO dto : allPermissionDTOs) {
            permissionMap.put(dto.getPerCode(), dto);
        }
        // 组装树状结构
        for (PermissionDTO dto : allPermissionDTOs) {
            String parentCode = dto.getPerParentCode();
            // 如果找到父节点，并且父节点在权限列表中
            if (parentCode != null && permissionMap.containsKey(parentCode)) {
                PermissionDTO parentDto = permissionMap.get(parentCode);
                if (parentDto.getChildren() == null) {
                    parentDto.setChildren(new java.util.ArrayList<>());
                }
                parentDto.getChildren().add(dto);
            }
        }
        // 为当前节点设置子节点
        sysPermission.setChildren(new java.util.ArrayList<>());
        for (PermissionDTO dto : allPermissionDTOs) {
            if (sysPermission.getPerCode().equals(dto.getPerParentCode())) {
                sysPermission.getChildren().add(dto);
            }
        }
    }
    @Override
    public PermissionDTO getByCode(String sysPerCode,List<String> perCodes) {
        perCodes.add(sysPerCode);
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPermission::getPerTypeCode, "0");
        queryWrapper.lambda().in(SysPermission::getPerCode, perCodes);
        queryWrapper.lambda().orderByAsc(SysPermission::getSeqNo);
        List<SysPermission> allPermissions = tgPermissionService.list(queryWrapper);
        SysPermission sysPermission = tgPermissionService.getOne(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getPerCode, sysPerCode));
        PermissionDTO result = BeanUtil.copyProperties(sysPermission, PermissionDTO.class);
        this.setTree(result,allPermissions);
        return result;
    }

    @Override
    public PermissionDTO getByCode(String perCode) {
        SysPermission sysPermission = tgPermissionService.getOne(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getPerCode, perCode));
        PermissionDTO result = BeanUtil.copyProperties(sysPermission, PermissionDTO.class);
        QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysPermission::getPerTypeCode, "0");
        queryWrapper.lambda().orderByAsc(SysPermission::getSeqNo);
        List<SysPermission> allPermissions = tgPermissionService.list(queryWrapper);
        this.setTree(result,allPermissions);
        return result;
    }


}
