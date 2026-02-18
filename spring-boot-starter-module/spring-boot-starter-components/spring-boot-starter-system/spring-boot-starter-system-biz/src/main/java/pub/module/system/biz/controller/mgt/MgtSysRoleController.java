package pub.module.system.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.curd.entity.SysRole;
import pub.module.system.curd.entity.SysRolePermission;
import pub.module.system.curd.service.SysRolePermissionService;
import pub.module.system.curd.service.SysRoleService;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * 角色 Controller
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/mgt/sysRole")
@Slf4j
public class MgtSysRoleController {
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysRolePermissionService sysRolePermissionService;


    @Operation(summary = "角色 - 分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysRole>> queryPageList(SysRole sysRole,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysRole> queryWrapper = WebQueryUtil.buildQuery(sysRole);
        Page<SysRole> page = new Page<>(pageNo, pageSize);
        IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "角色 - 添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysRole sysRole) {

        sysRoleService.save(sysRole);
        return Result.ok("添加成功！");
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class EditRoleVO extends SysRole {
        List<String> sysRolePermissionList;
    }

    @Operation(summary = "角色 - 编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody EditRoleVO editRoleVO) {
        sysRoleService.updateById(editRoleVO);
        QueryWrapper<SysRolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRolePermission::getRoleCode, editRoleVO.getRoleCode());
        sysRolePermissionService.remove(queryWrapper);
        for (String perCode : editRoleVO.getSysRolePermissionList()) {
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setPerCode(perCode);
            sysRolePermission.setRoleCode(editRoleVO.getRoleCode());
            sysRolePermissionService.save(sysRolePermission);
        }
        return Result.ok("编辑成功!");
    }

    @Operation(summary="管户关系 - 批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.sysRoleService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }


    @Operation(summary = "角色 - 批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
        this.sysRoleService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SysRoleVO extends SysRole {
        private List<SysRolePermission> sysRolePermissionList;
    }

    @Operation(summary = "角色 - 通过id查询")
    @GetMapping(value = "/queryByCode")
    public Result<SysRoleVO> queryByCode(@RequestParam(name = "code") String code) {
        SysRole sysRole = sysRoleService.getByCode(code);
        QueryWrapper<SysRolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRolePermission::getRoleCode, code);
        List<SysRolePermission> sysRolePermissionList = sysRolePermissionService.list(queryWrapper);
        SysRoleVO sysRoleVO = BeanUtil.copyProperties(sysRole, SysRoleVO.class);
        sysRoleVO.setSysRolePermissionList(sysRolePermissionList);
        return Result.ok(sysRoleVO);
    }


}