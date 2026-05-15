package pub.module.file.biz.controller.mgt;

import java.util.Collection;

import pub.module.file.curd.entity.BizFile;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.file.curd.service.BizFileService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * 管理端-文件
 *
 * @author tg
 *  2026-03-09 07:28:53
 */
@Tag(name="管理端-文件")
@RestController
@RequestMapping("/mgt/file/file")
@Slf4j
public class MgtFileController{
        @Resource
        private BizFileService bizFileService;


        @Operation(summary="管理端-文件分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<BizFile>> queryPageList(BizFile bizFile,
                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<BizFile> queryWrapper = WebQueryUtil.buildQuery(bizFile);
            Page<BizFile> page = new Page<>(pageNo, pageSize);
            IPage<BizFile> pageList = bizFileService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-文件添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody BizFile bizFile) {

                bizFileService.save(bizFile);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-文件编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody BizFile bizFile) {
                bizFileService.updateById(bizFile);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-文件批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.bizFileService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-文件通过id查询")
        @GetMapping(value = "/queryById")
        public Result<BizFile> queryById(@RequestParam(name="id") String id) {
            BizFile bizFile = bizFileService.getById(id);
            return Result.ok(bizFile);
        }

}