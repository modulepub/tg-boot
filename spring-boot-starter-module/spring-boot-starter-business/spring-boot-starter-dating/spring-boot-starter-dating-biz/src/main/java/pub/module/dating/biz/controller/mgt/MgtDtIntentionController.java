package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.crud.entity.DtIntention;
import pub.module.dating.crud.service.DtIntentionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-交友意向
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="管理端-交友意向")
@RestController
@RequestMapping("/mgt/dating/dtIntention")
@Slf4j
public class MgtDtIntentionController{
        @Resource
        private DtIntentionService dtIntentionService;


        @Operation(summary="管理端-交友意向分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtIntention>> queryPageList(DtIntention dtIntention,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtIntention> queryWrapper = WebQueryUtil.buildQuery(dtIntention);
            Page<DtIntention> page = new Page<>(pageNo, pageSize);
            IPage<DtIntention> pageList = dtIntentionService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-交友意向添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtIntention dtIntention) {

                dtIntentionService.save(dtIntention);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-交友意向编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtIntention dtIntention) {
                dtIntentionService.updateById(dtIntention);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-交友意向批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtIntentionService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-交友意向通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtIntention> queryById(@RequestParam(name="id") String id) {
            DtIntention dtIntention = dtIntentionService.getById(id);
            return Result.ok(dtIntention);
        }

}