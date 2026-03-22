package pub.module.file.biz.controller.mgt;

import java.util.Collection;

import pub.module.file.curd.entity.BizFile;
import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

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
 * 文件 Controller
 *
 * @author tg
 *  2026-03-09 07:28:53
 */
@Tag(name="文件 CURD 处理器")
@RestController
@RequestMapping("/mgt/file/file")
@Slf4j
public class MgtFileController{
        @Resource
        private BizFileService bizFileService;


        @Operation(summary="文件 - 分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<BizFile>> queryPageList(BizFile bizFile,
                                                    @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<BizFile> queryWrapper = WebQueryUtil.buildQuery(bizFile);
            Page<BizFile> page = new Page<>(pageNo, pageSize);
            IPage<BizFile> pageList = bizFileService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="文件 - 添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody BizFile bizFile) {

                bizFileService.save(bizFile);
            return Result.ok("添加成功！");
        }

        @Operation(summary="文件 - 编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody BizFile bizFile) {
                bizFileService.updateById(bizFile);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="文件 - 批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.bizFileService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="文件 - 通过id查询")
        @GetMapping(value = "/queryById")
        public Result<BizFile> queryById(@RequestParam(name="id") String id) {
            BizFile bizFile = bizFileService.getById(id);
            return Result.ok(bizFile);
        }

}