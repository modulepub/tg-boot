package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.service.DtContactApplyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-联系人申请表
 *
 * @author tg
 *  2026-05-03 03:39:43
 */
@Tag(name="管理端-联系人申请表")
@RestController
@RequestMapping("/mgt/dating/dtContactApply")
@Slf4j
public class MgtDtContactApplyController{
        @Resource
        private DtContactApplyService dtContactApplyService;


        @Operation(summary="管理端-联系人申请表-分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtContactApply>> queryPageList(DtContactApply dtContactApply,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtContactApply> queryWrapper = WebQueryUtil.buildQuery(dtContactApply);
            Page<DtContactApply> page = new Page<>(pageNo, pageSize);
            IPage<DtContactApply> pageList = dtContactApplyService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-联系人申请表-添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtContactApply dtContactApply) {
                dtContactApplyService.save(dtContactApply);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-联系人申请表-编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtContactApply dtContactApply) {
                dtContactApplyService.updateById(dtContactApply);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-联系人申请表-批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtContactApplyService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-联系人申请表-通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtContactApply> queryById(@RequestParam(name="id") String id) {
            DtContactApply dtContactApply = dtContactApplyService.getById(id);
            return Result.ok(dtContactApply);
        }

}