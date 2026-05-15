package pub.module.dating.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dating.curd.entity.DtMatchmakingCompany;
import pub.module.dating.curd.service.DtMatchmakingCompanyService;

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
 *  2026-03-22 13:32:45
 */
@Tag(name="管理端-红娘信息")
@RestController
@RequestMapping("/mgt/dating/dtMatchmakingCompany")
@Slf4j
public class MgtDtMatchmakingCompanyController{
        @Resource
        private DtMatchmakingCompanyService dtMatchmakingCompanyService;


        @Operation(summary="管理端-红娘信息分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<DtMatchmakingCompany>> queryPageList(DtMatchmakingCompany dtMatchmakingCompany,
                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<DtMatchmakingCompany> queryWrapper = WebQueryUtil.buildQuery(dtMatchmakingCompany);
            Page<DtMatchmakingCompany> page = new Page<>(pageNo, pageSize);
            IPage<DtMatchmakingCompany> pageList = dtMatchmakingCompanyService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-红娘信息添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody DtMatchmakingCompany dtMatchmakingCompany) {

                dtMatchmakingCompanyService.save(dtMatchmakingCompany);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-红娘信息编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody DtMatchmakingCompany dtMatchmakingCompany) {
                dtMatchmakingCompanyService.updateById(dtMatchmakingCompany);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-红娘信息批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.dtMatchmakingCompanyService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-红娘信息通过id查询")
        @GetMapping(value = "/queryById")
        public Result<DtMatchmakingCompany> queryById(@RequestParam(name="id") String id) {
            DtMatchmakingCompany dtMatchmakingCompany = dtMatchmakingCompanyService.getById(id);
            return Result.ok(dtMatchmakingCompany);
        }

}