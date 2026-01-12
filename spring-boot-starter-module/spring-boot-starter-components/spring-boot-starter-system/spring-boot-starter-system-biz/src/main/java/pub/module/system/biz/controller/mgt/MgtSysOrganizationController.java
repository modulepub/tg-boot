package pub.module.system.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
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
import pub.module.system.api.service.BizSysOrganizationService;
import pub.module.system.api.service.dto.OrganizationDTO;
import pub.module.system.curd.entity.SysOrganization;
import pub.module.system.curd.service.SysOrganizationService;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import java.util.List;


/**
 * 组织机构 Controller
 *
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name = "组织机构管理")
@RestController
@RequestMapping("/mgt/sysOrganization")
@Slf4j
public class MgtSysOrganizationController {
    @Resource
    private BizSysOrganizationService bizSysOrganizationService;
    @Resource
    private SysOrganizationService sysOrganizationService;


    @Schema(description = "组织机构树 VO")
    @Data
    public static class TreeVO {
        @Schema(description = "机构编码")
        private String orgCode;
    }
    @Operation(summary = "组织机构树 - 分页列表查询")
    @GetMapping(value = "/tree")
    public Result<OrganizationDTO> tree(TreeVO treeVO) {
        return Result.ok(bizSysOrganizationService.getByCode(treeVO.getOrgCode()));
    }

    @Operation(summary = "公司 - 分页列表查询")
    @GetMapping(value = "/listCompany")
    public Result<List<OrganizationDTO>> listCompany() {
        return Result.ok(bizSysOrganizationService.listRootCompany());
    }

    /**
     * Queries and returns paginated list of organizations with tree structure
     */
    @Operation(summary="组织机构管理 - 分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OrganizationDTO>> queryPageList(SysOrganization sysOrganization,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        QueryWrapper<SysOrganization> queryWrapper = WebQueryUtil.buildQuery(sysOrganization);
        queryWrapper.lambda().isNull(SysOrganization::getOrgParentCode);
        Page<SysOrganization> page = new Page<>(pageNo, pageSize);
        IPage<SysOrganization> pageList = sysOrganizationService.page(page, queryWrapper);
        IPage<OrganizationDTO> resultPage = pageList.convert(sysOrganizationItem -> {
            OrganizationDTO organizationDTO = BeanUtil.copyProperties(sysOrganizationItem, OrganizationDTO.class);
            bizSysOrganizationService.setTree(organizationDTO);
            return organizationDTO;
        });
        return Result.ok(resultPage);
    }
}