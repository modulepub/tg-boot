package pub.module.system.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pub.module.cache.api.service.BizCacheService;
import pub.module.system.api.service.BizSysPermissionService;
import pub.module.system.api.service.BizSysRolePermissionService;
import pub.module.system.api.service.BizSysUserOrganizationRoleService;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.curd.entity.SysPermission;
import pub.module.system.curd.service.SysPermissionService;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 菜单管理 Controller
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@RequestMapping("/mgt/sysPermission")
@Tag(name = "菜单管理")
@AllArgsConstructor
public class MgtPermissionController {
    @Resource
    BizSysPermissionService bizSysPermissionService;
    @Resource
    SysPermissionService sysPermissionService;
    @Resource
    BizCacheService bizCacheService;
    @Resource
    BizSysUserOrganizationRoleService bizSysUserOrganizationRoleService;
    @Resource
    BizSysRolePermissionService bizSysRolePermissionService;
    @GetMapping("/getByCode")
    @Operation(summary = "菜单导航")
    public Result<PermissionDTO> listAll(@RequestParam(name="code") String code) {
        PermissionDTO PermissionDTO = bizSysPermissionService.getByCode(code);
        return Result.ok(PermissionDTO);
    }
}