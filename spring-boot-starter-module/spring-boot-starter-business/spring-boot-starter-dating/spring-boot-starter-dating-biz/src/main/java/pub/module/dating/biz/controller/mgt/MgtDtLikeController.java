package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.dating.curd.entity.DtLike;
import pub.module.dating.curd.service.DtLikeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 喜欢 Controller
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="喜欢 CURD 处理器")
@RestController
@RequestMapping("/mgt/dating/dtLike")
@Slf4j
public class MgtDtLikeController{
        @Resource
        private DtLikeService dtLikeService;


        @Operation(summary="喜欢 - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtLike>> queryPageList(DtLike dtLike,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtLike> queryWrapper = WebQueryUtil.buildQuery(dtLike);
            Page<DtLike> page = new Page<>(pageNo, pageSize);
            IPage<DtLike> pageList = dtLikeService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="喜欢 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtLike dtLike) {

                dtLikeService.save(dtLike);
            return Result.ok("添加成功！");
        }

        @Operation(summary="喜欢 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtLike dtLike) {
                dtLikeService.updateById(dtLike);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="喜欢 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtLikeService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="喜欢 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtLike> queryById(@RequestParam(name="id") String id) {
            DtLike dtLike = dtLikeService.getById(id);
            return Result.ok(dtLike);
        }

}