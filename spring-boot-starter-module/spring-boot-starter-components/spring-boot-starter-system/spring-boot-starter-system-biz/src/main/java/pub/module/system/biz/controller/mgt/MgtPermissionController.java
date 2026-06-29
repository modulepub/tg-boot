package pub.module.system.biz.controller.mgt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.ApiSysPermissionService;
import pub.module.system.api.service.dto.PermissionDTO;
import pub.module.system.crud.entity.SysPermission;
import pub.module.system.crud.service.SysPermissionService;
import pub.module.common.model.vo.Result;

import java.util.Collection;
import java.util.List;


/**
 * 管理端-菜单管理
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@RestController
@RequestMapping("/mgt/sysPermission")
@Tag(name = "管理端-菜单管理")
@AllArgsConstructor
public class MgtPermissionController {
    @Resource
    ApiSysPermissionService apiSysPermissionService;
    @Resource
    SysPermissionService sysPermissionService;

    @GetMapping("/getTree")
    @Operation(summary = "管理端-菜单导航")
    public Result<PermissionDTO> listAll(@RequestParam(name = "code") String perCode) {
        List<PermissionDTO> permissionDTOList = apiSysPermissionService.getPermissions();
        return Result.ok(apiSysPermissionService.buildTree(perCode, permissionDTOList));
    }
    @Operation(summary="管理端-菜单管理通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysPermission> queryById(@RequestParam(name="id") String id) {
        SysPermission sysPermission = sysPermissionService.getById(id);
        return Result.ok(sysPermission);
    }
    @Operation(summary="管理端-菜单管理添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysPermission sysPermission) {
        sysPermissionService.save(sysPermission);
        return Result.ok("添加成功！");
    }

    @Operation(summary="管理端-菜单管理编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysPermission sysPermission) {
        sysPermissionService.updateById(sysPermission);
        return Result.ok("编辑成功!");
    }

    @Operation(summary="管理端-菜单管理批量删除")
    @PostMapping(value = "/delete")
    public Result<String> delete(@RequestBody Collection<String> list) {
        this.sysPermissionService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }
}