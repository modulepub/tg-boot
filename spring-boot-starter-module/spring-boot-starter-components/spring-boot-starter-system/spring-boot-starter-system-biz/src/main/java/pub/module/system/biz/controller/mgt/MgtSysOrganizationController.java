package pub.module.system.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.ApiSysOrganizationService;
import pub.module.system.api.service.dto.OrganizationDTO;
import pub.module.system.crud.entity.SysOrganization;
import pub.module.system.crud.service.SysOrganizationService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

import java.util.Collection;
import java.util.List;


/**
 * 管理端-组织机构
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@Tag(name = "管理端-组织机构管理")
@RestController
@RequestMapping("/mgt/sysOrganization")
@Slf4j
public class MgtSysOrganizationController {
    @Resource
    private ApiSysOrganizationService apiSysOrganizationService;
    @Resource
    private SysOrganizationService sysOrganizationService;


    @Schema(description = "管理端-组织机构树 VO")
    @Data
    public static class TreeVO {
        @Schema(description = "管理端-机构编码")
        private String orgCode;
    }

    @Operation(summary = "管理端-组织机构树分页列表查询")
    @GetMapping(value = "/tree")
    public Result<OrganizationDTO> tree(TreeVO treeVO) {
        return Result.ok(apiSysOrganizationService.getByCode(treeVO.getOrgCode()));
    }

    @Operation(summary = "管理端-公司分页列表查询")
    @GetMapping(value = "/listCompany")
    public Result<List<OrganizationDTO>> listCompany() {
        return Result.ok(apiSysOrganizationService.listRootCompany());
    }

    @Operation(summary = "管理端-组织机构树列表查询")
    @GetMapping(value = "/listTree")
    public Result<List<OrganizationDTO>> listTree() {
        QueryWrapper<SysOrganization> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(SysOrganization::getOrgParentCode).orderByAsc(SysOrganization::getSeqNo);
        List<SysOrganization> list = sysOrganizationService.list(queryWrapper);
        List<OrganizationDTO> result = BeanUtil.copyToList(list, OrganizationDTO.class);
        for (OrganizationDTO item : result) {
            apiSysOrganizationService.setTree(item);
        }
        return Result.ok(result);
    }

    /**
     * Queries and returns paginated list of organizations with tree structure
     */
    @Operation(summary = "管理端-组织机构管理分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OrganizationDTO>> queryPageList(SysOrganization sysOrganization,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysOrganization> queryWrapper = WebQueryUtil.buildQuery(sysOrganization);
        queryWrapper.lambda().isNull(SysOrganization::getOrgParentCode);
        Page<SysOrganization> page = new Page<>(pageNo, pageSize);
        IPage<SysOrganization> pageList = sysOrganizationService.page(page, queryWrapper);
        IPage<OrganizationDTO> resultPage = pageList.convert(sysOrganizationItem -> {
            OrganizationDTO organizationDTO = BeanUtil.copyProperties(sysOrganizationItem, OrganizationDTO.class);
            apiSysOrganizationService.setTree(organizationDTO);
            return organizationDTO;
        });
        return Result.ok(resultPage);
    }

    @Operation(summary = "管理端-组织机构添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysOrganization sysOrganization) {

        sysOrganizationService.save(sysOrganization);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-组织机构编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody SysOrganization sysOrganization) {
        sysOrganizationService.updateById(sysOrganization);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-组织机构调机构")
    @PostMapping(value = "/adjust")
    public Result<String> adjust(@RequestBody SysOrganization sysOrganization) {
        Assert.notBlank(sysOrganization.getId(), "机构ID不能为空");
        SysOrganization existing = sysOrganizationService.getById(sysOrganization.getId());
        existing.setOrgParentCode(StrUtil.isBlank(sysOrganization.getOrgParentCode()) ? null : sysOrganization.getOrgParentCode());
        sysOrganizationService.updateById(existing);
        return Result.ok("调机构成功!");
    }

    @Operation(summary = "管理端-组织机构批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.sysOrganizationService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-组织机构通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysOrganization> queryById(@RequestParam(name = "id") String id) {
        SysOrganization sysOrganization = sysOrganizationService.getById(id);
        return Result.ok(sysOrganization);
    }

}