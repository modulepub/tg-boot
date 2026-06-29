package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-客户红娘关系
 *
 * @author tg
 *  2026-03-25 00:36:20
 */
@Tag(name="管理端-客户红娘关系")
@RestController
@RequestMapping("/mgt/dating/dtCusMatchmakerRel")
@Slf4j
public class MgtDtCusMatchmakerRelController{
        @Resource
        private DtCusMatchmakerRelService dtCusMatchmakerRelService;


        @Operation(summary="管理端-客户红娘关系分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtCusMatchmakerRel>> queryPageList(DtCusMatchmakerRel dtCusMatchmakerRel,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
            Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
            IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-客户红娘关系添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtCusMatchmakerRel dtCusMatchmakerRel) {

                dtCusMatchmakerRelService.save(dtCusMatchmakerRel);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-客户红娘关系编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtCusMatchmakerRel dtCusMatchmakerRel) {
                dtCusMatchmakerRelService.updateById(dtCusMatchmakerRel);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-客户红娘关系批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtCusMatchmakerRelService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-客户红娘关系通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtCusMatchmakerRel> queryById(@RequestParam(name="id") String id) {
            DtCusMatchmakerRel dtCusMatchmakerRel = dtCusMatchmakerRelService.getById(id);
            return Result.ok(dtCusMatchmakerRel);
        }

}