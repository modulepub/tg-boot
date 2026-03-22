package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.dating.curd.entity.DtCusMatchmaker;
import pub.module.dating.curd.service.DtCusMatchmakerService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 我的红娘 Controller
 *
 * @author tg
 *  2026-01-07 23:30:24
 */
@Tag(name="我的红娘 CURD 处理器")
@RestController
@RequestMapping("/mgt/dating/dtCusMatchmaker")
@Slf4j
public class MgtDtCusMatchmakerController{
        @Resource
        private DtCusMatchmakerService dtCusMatchmakerService;


        @Operation(summary="我的红娘 - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtCusMatchmaker>> queryPageList(DtCusMatchmaker dtCusMatchmaker,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtCusMatchmaker> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmaker);
            Page<DtCusMatchmaker> page = new Page<>(pageNo, pageSize);
            IPage<DtCusMatchmaker> pageList = dtCusMatchmakerService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="我的红娘 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtCusMatchmaker dtCusMatchmaker) {

                dtCusMatchmakerService.save(dtCusMatchmaker);
            return Result.ok("添加成功！");
        }

        @Operation(summary="我的红娘 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtCusMatchmaker dtCusMatchmaker) {
                dtCusMatchmakerService.updateById(dtCusMatchmaker);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="我的红娘 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtCusMatchmakerService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="我的红娘 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtCusMatchmaker> queryById(@RequestParam(name="id") String id) {
            DtCusMatchmaker dtCusMatchmaker = dtCusMatchmakerService.getById(id);
            return Result.ok(dtCusMatchmaker);
        }

}