package pub.module.system.biz.controller.mgt;

import java.util.Collection;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.system.api.service.BizSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.curd.entity.SysOrganization;
import pub.module.system.curd.entity.SysUser;
import pub.module.system.curd.service.SysOrganizationService;
import pub.module.system.curd.service.SysUserService;
import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.system.curd.entity.SysUserOrganization;
import pub.module.system.curd.service.SysUserOrganizationService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * 用户所属组织机构 Controller
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
@Tag(name = "用户所属组织机构 CURD 处理器")
@RestController
@RequestMapping("/mgt/sysUserOrganization")
@Slf4j
public class MgtSysUserOrganizationController {
    @Resource
    private SysUserOrganizationService sysUserOrganizationService;
    @Resource
    private SysOrganizationService sysOrganizationService;
    @Resource
    private BizSysUserService bizSysUserService;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ListByUserVO extends SysOrganization {
        boolean selected = false;
    }

    @Operation(summary = "用户所属组织机构 - 分页列表查询")
    @GetMapping(value = "/listByUser")
    public Result<List<ListByUserVO>> listByUser() {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        String sql = "select org_code from sys_user_organization where user_code = '${userCode}'";
        QueryWrapper<SysOrganization> queryWrapper = new QueryWrapper<>();
        sql = sql.replace("${userCode}", userDTO.getUserCode());
        queryWrapper.lambda().inSql(SysOrganization::getOrgCode, sql);
        List<SysOrganization> list = sysOrganizationService.list(queryWrapper);
        List<ListByUserVO> result = BeanUtil.copyToList(list, ListByUserVO.class);
        for (ListByUserVO listByUserVO : result) {
            if (listByUserVO.getOrgCode().equals(userDTO.getUserOrgCode())) {
                listByUserVO.setSelected(true);
            }
        }
        return Result.ok(result);
    }

    @Operation(summary = "用户所属组织机构 - 变更组织机构")
    @PostMapping(value = "/change")
    public Result<BizSysUserService.LoginDTO> change(@RequestBody SysUserOrganization sysUserOrganization) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        return Result.ok(bizSysUserService.changeOrg(userDTO.getUserCode(),sysUserOrganization.getOrgCode()));
    }

    @Operation(summary = "用户所属组织机构 - 分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysUserOrganization>> queryPageList(SysUserOrganization sysUserOrganization,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysUserOrganization> queryWrapper = WebQueryUtil.buildQuery(sysUserOrganization);
        Page<SysUserOrganization> page = new Page<>(pageNo, pageSize);
        IPage<SysUserOrganization> pageList = sysUserOrganizationService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户所属组织机构 - 添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysUserOrganization sysUserOrganization) {

        sysUserOrganizationService.save(sysUserOrganization);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "用户所属组织机构 - 编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysUserOrganization sysUserOrganization) {
        sysUserOrganizationService.updateById(sysUserOrganization);
        return Result.ok("编辑成功!");
    }


    @Operation(summary = "用户所属组织机构 - 批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.sysUserOrganizationService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "用户所属组织机构 - 通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysUserOrganization> queryById(@RequestParam(name = "id") String id) {
        SysUserOrganization sysUserOrganization = sysUserOrganizationService.getById(id);
        return Result.ok(sysUserOrganization);
    }

}