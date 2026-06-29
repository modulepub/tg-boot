package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.dating.api.service.ApiDtMatchmakingCompanyMgtService;
import pub.module.dating.api.service.dto.MatchmakingCompanyAuditRejectVO;
import pub.module.dating.api.service.dto.MatchmakingCompanyMgtEditVO;
import pub.module.dating.api.service.dto.MatchmakingCompanySetAdminVO;
import pub.module.dating.crud.entity.DtMatchmakingCompany;
import pub.module.dating.crud.service.DtMatchmakingCompanyService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-婚介公司
 *
 * @author tg
 *  2026-03-22 13:32:45
 */
@Tag(name="管理端-婚介公司")
@RestController
@RequestMapping("/mgt/dating/dtMatchmakingCompany")
@Slf4j
public class MgtDtMatchmakingCompanyController{
        @Resource
        private DtMatchmakingCompanyService dtMatchmakingCompanyService;
        @Resource
        private ApiDtMatchmakingCompanyMgtService apiDtMatchmakingCompanyMgtService;


        @Operation(summary="管理端-婚介公司分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtMatchmakingCompany>> queryPageList(DtMatchmakingCompany dtMatchmakingCompany,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtMatchmakingCompany> queryWrapper = WebQueryUtil.buildQuery(dtMatchmakingCompany);
            queryWrapper.orderByDesc("create_time");
            Page<DtMatchmakingCompany> page = new Page<>(pageNo, pageSize);
            IPage<DtMatchmakingCompany> pageList = dtMatchmakingCompanyService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-婚介公司添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtMatchmakingCompany dtMatchmakingCompany) {

                dtMatchmakingCompanyService.save(dtMatchmakingCompany);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-婚介公司编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtMatchmakingCompany dtMatchmakingCompany) {
                dtMatchmakingCompanyService.updateById(dtMatchmakingCompany);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-婚介公司批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtMatchmakingCompanyService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-婚介公司通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtMatchmakingCompany> queryById(@RequestParam(name="id") String id) {
            DtMatchmakingCompany dtMatchmakingCompany = dtMatchmakingCompanyService.getById(id);
            return Result.ok(dtMatchmakingCompany);
        }

        @Operation(summary = "管理端-企业入驻审核通过")
        @PostMapping("/approve")
        public Result<String> approve(@RequestParam("id") String id) {
            UserDTO user = UserUtil.getCurrentSysUser();
            String auditBy = user != null ? user.getUserCode() : null;
            apiDtMatchmakingCompanyMgtService.approve(id, auditBy);
            return Result.ok("审核通过");
        }

        @Operation(summary = "管理端-企业入驻审核驳回")
        @PostMapping("/reject")
        public Result<String> reject(@RequestBody MatchmakingCompanyAuditRejectVO vo) {
            UserDTO user = UserUtil.getCurrentSysUser();
            String auditBy = user != null ? user.getUserCode() : null;
            apiDtMatchmakingCompanyMgtService.reject(vo.getId(), vo.getRejectReason(), auditBy);
            return Result.ok("已驳回");
        }

        @Operation(summary = "管理端-代提交企业入驻审核")
        @PostMapping("/submitForReview")
        public Result<String> submitForReview(@RequestParam("id") String id) {
            apiDtMatchmakingCompanyMgtService.submitForReview(id);
            return Result.ok("已提交审核");
        }

        @Operation(summary = "管理端-编辑企业入驻资料")
        @PostMapping("/updateApply")
        public Result<String> updateApply(@RequestBody MatchmakingCompanyMgtEditVO vo) {
            apiDtMatchmakingCompanyMgtService.updateApplyInfo(vo);
            return Result.ok("保存成功");
        }

        @Operation(summary = "管理端-设置企业管理员")
        @PostMapping("/setAdmin")
        public Result<String> setAdmin(@RequestBody MatchmakingCompanySetAdminVO vo) {
            apiDtMatchmakingCompanyMgtService.setAdmin(vo.getId(), vo.getAdminUserCode());
            return Result.ok("设置成功");
        }

}