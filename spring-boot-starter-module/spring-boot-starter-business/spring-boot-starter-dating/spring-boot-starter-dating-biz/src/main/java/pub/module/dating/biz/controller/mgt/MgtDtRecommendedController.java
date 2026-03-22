package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.dating.curd.entity.DtRecommended;
import pub.module.dating.curd.service.DtRecommendedService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 推荐 Controller
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="推荐 CURD 处理器")
@RestController
@RequestMapping("/mgt/dating/dtRecommended")
@Slf4j
public class MgtDtRecommendedController{
        @Resource
        private DtRecommendedService dtRecommendedService;


        @Operation(summary="推荐 - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtRecommended>> queryPageList(DtRecommended dtRecommended,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtRecommended> queryWrapper = WebQueryUtil.buildQuery(dtRecommended);
            Page<DtRecommended> page = new Page<>(pageNo, pageSize);
            IPage<DtRecommended> pageList = dtRecommendedService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="推荐 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtRecommended dtRecommended) {

                dtRecommendedService.save(dtRecommended);
            return Result.ok("添加成功！");
        }

        @Operation(summary="推荐 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtRecommended dtRecommended) {
                dtRecommendedService.updateById(dtRecommended);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="推荐 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtRecommendedService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="推荐 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtRecommended> queryById(@RequestParam(name="id") String id) {
            DtRecommended dtRecommended = dtRecommendedService.getById(id);
            return Result.ok(dtRecommended);
        }

}