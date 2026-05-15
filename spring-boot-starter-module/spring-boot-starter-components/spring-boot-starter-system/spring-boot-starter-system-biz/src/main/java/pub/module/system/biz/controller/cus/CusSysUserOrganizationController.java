package pub.module.system.biz.controller.cus;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.curd.entity.SysOrganization;
import pub.module.system.curd.entity.SysUserOrganization;
import pub.module.system.curd.service.SysOrganizationService;
import pub.module.system.curd.service.SysUserOrganizationService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

import java.util.Collection;
import java.util.List;


@Tag(name = "用户端-所属机构关联关系")
@RestController
@RequestMapping("/cus/sysUserOrganization")
@Slf4j
public class CusSysUserOrganizationController {
    @Resource
    private SysUserOrganizationService sysUserOrganizationService;
    @Resource
    private SysOrganizationService sysOrganizationService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ListByUserResVO extends SysOrganization {
        boolean selected = false;
    }

    @Operation(summary = "用户端-所属组织机构分页列表查询")
    @GetMapping(value = "/listByUser")
    public Result<List<ListByUserResVO>> listByUser() {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        String sql = "select org_code from sys_user_organization where deleted = 0 and user_code = '${userCode}'";
        QueryWrapper<SysOrganization> queryWrapper = new QueryWrapper<>();
        sql = sql.replace("${userCode}", userDTO.getUserCode());
        queryWrapper.lambda().inSql(SysOrganization::getOrgCode, sql);
        List<SysOrganization> list = sysOrganizationService.list(queryWrapper);
        List<ListByUserResVO> result = BeanUtil.copyToList(list, ListByUserResVO.class);
        for (ListByUserResVO resVO : result) {
            if(resVO.getOrgCode().equals(userDTO.getUserOrgCode())){
                resVO.setSelected(true);
            }
        }
        return Result.ok(result);
    }

    @Data
    public static class ChangeCurrentOrgVO{
        @Schema(description = "用户端-机构编码")
        private String orgCode;
    }
    @Operation(summary = "用户端-所属组织机构变更组织机构")
    @PostMapping(value = "/changeCurrentOrg")
    public Result<ApiSysUserService.LoginDTO> changeCurrentOrg(@RequestBody ChangeCurrentOrgVO changeCurrentOrgVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        return Result.ok(apiSysUserService.changeOrg(userDTO.getUserCode(),changeCurrentOrgVO.getOrgCode()));
    }

    @Operation(summary = "用户端-所属组织机构分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysUserOrganization>> queryPageList(SysUserOrganization sysUserOrganization,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysUserOrganization> queryWrapper = WebQueryUtil.buildQuery(sysUserOrganization);
        Page<SysUserOrganization> page = new Page<>(pageNo, pageSize);
        IPage<SysUserOrganization> pageList = sysUserOrganizationService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-所属组织机构添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysUserOrganization sysUserOrganization) {

        sysUserOrganizationService.save(sysUserOrganization);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "用户端-所属组织机构编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysUserOrganization sysUserOrganization) {
        sysUserOrganizationService.updateById(sysUserOrganization);
        return Result.ok("编辑成功!");
    }


    @Operation(summary = "用户端-所属组织机构批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.sysUserOrganizationService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "用户端-所属组织机构通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysUserOrganization> queryById(@RequestParam(name = "id") String id) {
        SysUserOrganization sysUserOrganization = sysUserOrganizationService.getById(id);
        return Result.ok(sysUserOrganization);
    }

}