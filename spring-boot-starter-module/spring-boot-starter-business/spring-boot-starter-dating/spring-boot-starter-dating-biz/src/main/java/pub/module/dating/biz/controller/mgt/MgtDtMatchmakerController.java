package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.api.service.ApiDtMatchmakerMgtService;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.dating.api.service.dto.MatchmakerAuditRejectVO;
import pub.module.dating.biz.service.InitGoodsService;
import pub.module.dating.biz.service.support.InitMatchmakerGoodsBatchResult;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtMatchmakerService;
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
 * 管理端-红娘信息
 *
 * @author tg
 *  2026-03-22 13:32:44
 */
@Tag(name="管理端-红娘信息")
@RestController
@RequestMapping("/mgt/dating/dtMatchmaker")
@Slf4j
public class MgtDtMatchmakerController{
        @Resource
        private DtMatchmakerService dtMatchmakerService;
        @Resource
        private ApiDtMatchmakerService apiDtMatchmakerService;
        @Resource
        private ApiDtMatchmakerMgtService apiDtMatchmakerMgtService;
        @Resource
        private InitGoodsService initGoodsService;


        @Operation(summary="管理端-红娘信息分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtMatchmaker>> queryPageList(DtMatchmaker dtMatchmaker,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtMatchmaker> queryWrapper = WebQueryUtil.buildQuery(dtMatchmaker);
            Page<DtMatchmaker> page = new Page<>(pageNo, pageSize);
            IPage<DtMatchmaker> pageList = dtMatchmakerService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-红娘信息添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtMatchmaker dtMatchmaker) {

                dtMatchmakerService.save(dtMatchmaker);
                apiDtMatchmakerService.syncUserRealNameFromMatchmaker(dtMatchmaker.getMkUserCode(), dtMatchmaker.getMkName());
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-红娘信息编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtMatchmaker dtMatchmaker) {
                dtMatchmakerService.updateById(dtMatchmaker);
                apiDtMatchmakerService.syncUserRealNameFromMatchmaker(dtMatchmaker.getMkUserCode(), dtMatchmaker.getMkName());
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-红娘信息批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtMatchmakerService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-红娘信息通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtMatchmaker> queryById(@RequestParam(name="id") String id) {
            DtMatchmaker dtMatchmaker = dtMatchmakerService.getById(id);
            return Result.ok(dtMatchmaker);
        }

        @Operation(summary = "管理端-红娘资质平台审核通过")
        @PostMapping("/approve")
        public Result<String> approve(@RequestParam("id") String id) {
            UserDTO user = UserUtil.getCurrentSysUser();
            String auditBy = user != null ? user.getUserCode() : null;
            apiDtMatchmakerMgtService.approve(id, auditBy);
            return Result.ok("审核通过");
        }

        @Operation(summary = "管理端-红娘资质平台审核驳回")
        @PostMapping("/reject")
        public Result<String> reject(@RequestBody MatchmakerAuditRejectVO vo) {
            UserDTO user = UserUtil.getCurrentSysUser();
            String auditBy = user != null ? user.getUserCode() : null;
            apiDtMatchmakerMgtService.reject(vo.getId(), vo.getRejectReason(), auditBy);
            return Result.ok("已驳回");
        }

        @Operation(summary = "管理端-红娘资质直接通过")
        @PostMapping("/directApprove")
        public Result<String> directApprove(@RequestParam("id") String id) {
            UserDTO user = UserUtil.getCurrentSysUser();
            String auditBy = user != null ? user.getUserCode() : null;
            apiDtMatchmakerMgtService.directApprove(id, auditBy);
            return Result.ok("已通过");
        }

        @Operation(summary = "管理端-红娘视频号审核通过")
        @PostMapping("/approveChannels")
        public Result<String> approveChannels(@RequestParam("id") String id) {
            UserDTO user = UserUtil.getCurrentSysUser();
            String auditBy = user != null ? user.getUserCode() : null;
            apiDtMatchmakerMgtService.approveChannels(id, auditBy);
            return Result.ok("审核通过");
        }

        @Operation(summary = "管理端-红娘视频号审核驳回")
        @PostMapping("/rejectChannels")
        public Result<String> rejectChannels(@RequestBody MatchmakerAuditRejectVO vo) {
            UserDTO user = UserUtil.getCurrentSysUser();
            String auditBy = user != null ? user.getUserCode() : null;
            apiDtMatchmakerMgtService.rejectChannels(vo.getId(), vo.getRejectReason(), auditBy);
            return Result.ok("已驳回");
        }

        @Operation(summary = "管理端-一键初始化全部已认证红娘默认服务商品")
        @PostMapping("/initAllGoods")
        public Result<InitMatchmakerGoodsBatchResult> initAllGoods() {
            return Result.ok(initGoodsService.initAllCertifiedMatchmakers());
        }

}