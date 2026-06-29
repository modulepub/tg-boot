package pub.module.system.biz.controller.mgt;

import java.util.Collection;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.springframework.transaction.annotation.Transactional;
import pub.module.system.api.service.dto.UserOrgRoleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.system.api.service.ApiSysUserOrganizationService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.OrganizationDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.crud.entity.SysOrganization;
import pub.module.system.crud.service.SysOrganizationService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.system.crud.entity.SysUserOrganization;
import pub.module.system.crud.service.SysUserOrganizationService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * 管理端-用户所属组织机构
 *
 * @author tg
 * 2026-01-04 13:16:25
 */
@Tag(name = "管理端-用户所属组织机构")
@RestController
@RequestMapping("/mgt/sysUserOrganization")
@Slf4j
public class MgtSysUserOrganizationController {
    @Resource
    private SysUserOrganizationService sysUserOrganizationService;
    @Resource
    private SysOrganizationService sysOrganizationService;
    @Resource
    private ApiSysUserOrganizationService apiSysUserOrganizationService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ListByUserResVO extends OrganizationDTO {
        boolean selected = false;
    }

    @Data
    public static class ListByUserReqVO {
        String userCode;
    }


    @Operation(summary = "管理端-用户所属组织机构分页列表查询")
    @GetMapping(value = "/listByUser")
    public Result<List<ListByUserResVO>> listByUser(ListByUserReqVO  listByUserReqVO) {
        Assert.notEmpty(listByUserReqVO.getUserCode(),"用户编码不能为空！");
        List<OrganizationDTO> list = apiSysUserOrganizationService.listOrganizationsByUserCode(listByUserReqVO.getUserCode());
        List<ListByUserResVO> result = BeanUtil.copyToList(list, ListByUserResVO.class);
        return Result.ok(result);
    }

    @Operation(summary = "管理端-用户机构角色列表")
    @GetMapping(value = "/listByUserCode")
    public Result<List<SysUserOrganization>> listByUserCode(SysUserOrganization sysUserOrganization) {
        Assert.notEmpty(sysUserOrganization.getUserCode(), "用户编码不能为空");
        QueryWrapper<SysUserOrganization> queryWrapper = new QueryWrapper<>(sysUserOrganization);
        queryWrapper.lambda()
                .eq(SysUserOrganization::getUserCode, sysUserOrganization.getUserCode())
                .orderByAsc(SysUserOrganization::getSeqNo)
                .orderByAsc(SysUserOrganization::getCreateTime);
        return Result.ok(sysUserOrganizationService.list(queryWrapper));
    }

    @Data
    public static class SaveUserOrgVO {
        private String userCode;
        private List<SysUserOrganization> sysUserOrganizationList;
    }

    @Operation(summary = "管理端-保存用户机构角色")
    @PostMapping(value = "/save")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> save(@RequestBody SaveUserOrgVO saveUserOrgVO) {
        Assert.notEmpty(saveUserOrgVO.getUserCode(), "用户编码不能为空");
        List<UserOrgRoleDTO> items = saveUserOrgVO.getSysUserOrganizationList() == null
                ? List.of()
                : saveUserOrgVO.getSysUserOrganizationList().stream()
                .filter(item -> item != null && StrUtil.isNotBlank(item.getOrgCode()) && StrUtil.isNotBlank(item.getRoleCode()))
                .map(item -> {
                    UserOrgRoleDTO dto = new UserOrgRoleDTO();
                    dto.setOrgCode(item.getOrgCode().trim());
                    dto.setRoleCode(item.getRoleCode().trim());
                    return dto;
                })
                .toList();
        apiSysUserOrganizationService.saveUserOrganizations(saveUserOrgVO.getUserCode(), items);
        return Result.ok("保存成功");
    }

    @Data
    public static class ChangeCurrentOrgVO{
        @Schema(description = "管理端-机构编码")
        private String orgCode;
    }
    @Operation(summary = "管理端-用户所属组织机构变更组织机构")
    @PostMapping(value = "/changeCurrentOrg")
    public Result<ApiSysUserService.LoginDTO> changeCurrentOrg(@RequestBody ChangeCurrentOrgVO changeCurrentOrgVO) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        return Result.ok(apiSysUserService.changeOrg(userDTO.getUserCode(),changeCurrentOrgVO.getOrgCode()));
    }

    @Operation(summary = "管理端-用户所属组织机构分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysUserOrganization>> queryPageList(SysUserOrganization sysUserOrganization,
                                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysUserOrganization> queryWrapper = WebQueryUtil.buildQuery(sysUserOrganization);
        Page<SysUserOrganization> page = new Page<>(pageNo, pageSize);
        IPage<SysUserOrganization> pageList = sysUserOrganizationService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-用户所属组织机构添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysUserOrganization sysUserOrganization) {

        sysUserOrganizationService.save(sysUserOrganization);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-用户所属组织机构编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysUserOrganization sysUserOrganization) {
        sysUserOrganizationService.updateById(sysUserOrganization);
        return Result.ok("编辑成功!");
    }


    @Operation(summary = "管理端-用户所属组织机构批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.sysUserOrganizationService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-用户所属组织机构通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysUserOrganization> queryById(@RequestParam(name = "id") String id) {
        SysUserOrganization sysUserOrganization = sysUserOrganizationService.getById(id);
        return Result.ok(sysUserOrganization);
    }

}