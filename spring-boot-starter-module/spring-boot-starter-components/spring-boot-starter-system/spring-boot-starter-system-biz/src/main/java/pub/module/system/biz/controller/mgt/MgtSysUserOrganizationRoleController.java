package pub.module.system.biz.controller.mgt;

import java.util.Collection;
import java.util.List;

import cn.hutool.core.lang.Assert;
import lombok.Data;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.system.curd.entity.SysUserOrganizationRole;
import pub.module.system.curd.service.SysUserOrganizationRoleService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-用户所属角色表
 *
 * @author tg
 *  2026-01-04 13:16:25
 */
@Tag(name="管理端-用户所属角色表")
@RestController
@RequestMapping("/mgt/sysUserOrganizationRole")
@Slf4j
public class MgtSysUserOrganizationRoleController{
        @Resource
        private SysUserOrganizationRoleService sysUserOrganizationRoleService;

    @Operation(summary="管理端-用户所属角色表分页列表查询")
    @GetMapping(value = "/listByUserCode")
    public Result<List<SysUserOrganizationRole>> listByUser(SysUserOrganizationRole sysUserOrganizationRole) {
        QueryWrapper<SysUserOrganizationRole> queryWrapper = new QueryWrapper<>(sysUserOrganizationRole);
        List<SysUserOrganizationRole> list = sysUserOrganizationRoleService.list( queryWrapper);
        return Result.ok(list);
    }

    @Data
    public static class SaveUORVO{
        String userCode;
        List<SysUserOrganizationRole> sysUserOrganizationRoleList;
    }
    @Operation(summary="管理端-用户所属角色表编辑")
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody SaveUORVO saveUORVO) {
        Assert.notEmpty(saveUORVO.getUserCode(),"userCode not null");
        sysUserOrganizationRoleService.remove(new QueryWrapper<SysUserOrganizationRole>().lambda().eq(SysUserOrganizationRole::getUserCode,saveUORVO.getUserCode()));
        saveUORVO.sysUserOrganizationRoleList.forEach(sysUserOrganizationRole -> sysUserOrganizationRole.setUserCode(saveUORVO.getUserCode()));
        sysUserOrganizationRoleService.saveBatch(saveUORVO.getSysUserOrganizationRoleList());
        return Result.ok("编辑成功!");
    }

        @Operation(summary="管理端-用户所属角色表分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<SysUserOrganizationRole>> queryPageList(SysUserOrganizationRole sysUserOrganizationRole,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<SysUserOrganizationRole> queryWrapper = WebQueryUtil.buildQuery(sysUserOrganizationRole);
            Page<SysUserOrganizationRole> page = new Page<>(pageNo, pageSize);
            IPage<SysUserOrganizationRole> pageList = sysUserOrganizationRoleService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-用户所属角色表添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody SysUserOrganizationRole sysUserOrganizationRole) {

                sysUserOrganizationRoleService.save(sysUserOrganizationRole);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-用户所属角色表编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody SysUserOrganizationRole sysUserOrganizationRole) {
                sysUserOrganizationRoleService.updateById(sysUserOrganizationRole);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-用户所属角色表批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.sysUserOrganizationRoleService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-用户所属角色表通过id查询")
        @GetMapping(value = "/queryById")
        public Result<SysUserOrganizationRole> queryById(@RequestParam(name="id") String id) {
            SysUserOrganizationRole sysUserOrganizationRole = sysUserOrganizationRoleService.getById(id);
            return Result.ok(sysUserOrganizationRole);
        }

}