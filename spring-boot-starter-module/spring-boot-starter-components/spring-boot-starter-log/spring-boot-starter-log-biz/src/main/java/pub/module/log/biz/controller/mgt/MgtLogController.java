package pub.module.log.biz.controller.mgt;

import java.util.Collection;

import pub.module.log.curd.entity.BizLog;
import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.log.curd.service.BizLogService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 日志表 Controller
 *
 * @author tg
 *  2026-01-12 01:41:07
 */
@Tag(name="日志表 CURD 处理器")
@RestController
@RequestMapping("/mgt/log/log")
@Slf4j
public class MgtLogController{
        @Resource
        private BizLogService bizLogService;


        @Operation(summary="日志表 - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<BizLog>> queryPageList(BizLog bizLog,
                                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<BizLog> queryWrapper = WebQueryUtil.buildQuery(bizLog);
            Page<BizLog> page = new Page<>(pageNo, pageSize);
            IPage<BizLog> pageList = bizLogService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="日志表 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody BizLog bizLog) {

                bizLogService.save(bizLog);
            return Result.ok("添加成功！");
        }

        @Operation(summary="日志表 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody BizLog bizLog) {
                bizLogService.updateById(bizLog);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="日志表 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.bizLogService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="日志表 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<BizLog> queryById(@RequestParam(name="id") String id) {
            BizLog bizLog = bizLogService.getById(id);
            return Result.ok(bizLog);
        }

}