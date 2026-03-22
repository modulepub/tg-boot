package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.dating.curd.entity.DtMatch;
import pub.module.dating.curd.service.DtMatchService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 匹配申请（牵线） Controller
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="匹配申请（牵线） CURD 处理器")
@RestController
@RequestMapping("/mgt/dating/dtMatch")
@Slf4j
public class MgtDtMatchController{
        @Resource
        private DtMatchService dtMatchService;


        @Operation(summary="匹配申请（牵线） - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtMatch>> queryPageList(DtMatch dtMatch,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtMatch> queryWrapper = WebQueryUtil.buildQuery(dtMatch);
            Page<DtMatch> page = new Page<>(pageNo, pageSize);
            IPage<DtMatch> pageList = dtMatchService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="匹配申请（牵线） - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtMatch dtMatch) {

                dtMatchService.save(dtMatch);
            return Result.ok("添加成功！");
        }

        @Operation(summary="匹配申请（牵线） - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtMatch dtMatch) {
                dtMatchService.updateById(dtMatch);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="匹配申请（牵线） - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtMatchService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="匹配申请（牵线） - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtMatch> queryById(@RequestParam(name="id") String id) {
            DtMatch dtMatch = dtMatchService.getById(id);
            return Result.ok(dtMatch);
        }

}