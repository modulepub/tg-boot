package pub.module.system.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
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

import java.util.*;


/**
 * 菜单管理 Controller
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@RequestMapping("/cus/sysPermission")
@Tag(name = "菜单管理")
@AllArgsConstructor
public class CusPermissionController {
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
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        List<String> roles = bizSysUserOrganizationRoleService.getRolesByOrgCodeAndUserCode(userDTO.getUserOrgCode(),userDTO.getUserCode());
        roles.add("-");
        List<String> permissions = bizSysRolePermissionService.getPermissionsByRoles(roles);
        permissions.add("-");
        PermissionDTO PermissionDTO = bizSysPermissionService.getByCode(code, permissions);
        return Result.ok(PermissionDTO);
    }

    @Operation(summary="菜单 - 分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysPermission>> queryPageList(SysPermission permission,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        QueryWrapper<SysPermission> queryWrapper = WebQueryUtil.buildQuery(permission);
        queryWrapper.lambda().orderByAsc(SysPermission::getSeqNo);
        Page<SysPermission> page = new Page<>(pageNo, pageSize);
        IPage<SysPermission> pageList = sysPermissionService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping("authority")
    @Operation(summary = "用户权限标识")
    public Result<Set<String>> authority() {
        Set<String> set = new HashSet<>();
        return Result.ok(set);
    }


    @Operation(summary="菜单管理 - 通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysPermission> queryById(@RequestParam(name="id") String id) {
        SysPermission sysPermission = sysPermissionService.getById(id);
        return Result.ok(sysPermission);
    }
    @Operation(summary="菜单管理 - 添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysPermission sysPermission) {
        sysPermissionService.save(sysPermission);
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        bizCacheService.delete("PermissionDTO::"+userCode);
        return Result.ok("添加成功！");
    }

    @Operation(summary="菜单管理 - 编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysPermission sysPermission) {
        sysPermissionService.updateById(sysPermission);
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        bizCacheService.delete("PermissionDTO::"+userCode);
        return Result.ok("编辑成功!");
    }

    @Operation(summary="菜单管理 - 批量删除")
    @PostMapping(value = "/delete")
    public Result<String> delete(@RequestBody Collection<String> list) {
        this.sysPermissionService.removeByIds(list);
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        bizCacheService.delete("PermissionDTO::"+userCode);
        return Result.ok("批量删除成功!");
    }
}