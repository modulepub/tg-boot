package pub.module.file.biz.controller.mgt;

import java.util.Collection;

import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import pub.module.file.curd.entity.BizConfig;
import pub.module.file.curd.service.BizConfigService;


/**
 * 管理端-CMS-节点
 *
 * @author tg
 *  2026-03-21 21:34:38
 */
@Tag(name="管理端-CMS-节点")
@RestController
@RequestMapping("/mgt/config/config")
@Slf4j
public class MgtConfigController{
        @Resource
        private BizConfigService bizConfigService;


        @Operation(summary="管理端-CMS-节点分页列表查询")
        @GetMapping(value = "/list")
        public Result<IPage<BizConfig>> queryPageList(BizConfig bizConfig,
                                                      @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                      @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
            QueryWrapper<BizConfig> queryWrapper = WebQueryUtil.buildQuery(bizConfig);
            Page<BizConfig> page = new Page<>(pageNo, pageSize);
            IPage<BizConfig> pageList = bizConfigService.page(page, queryWrapper);
            return Result.ok(pageList);
        }

        @Operation(summary="管理端-CMS-节点添加")
        @PostMapping(value = "/add")
        public Result<String> add(@RequestBody BizConfig bizConfig) {

                bizConfigService.save(bizConfig);
            return Result.ok("添加成功！");
        }

        @Operation(summary="管理端-CMS-节点编辑")
        @PostMapping(value = "/edit")
        public Result<String> edit(@RequestBody BizConfig bizConfig) {
                bizConfigService.updateById(bizConfig);
            return Result.ok("编辑成功!");
        }


        @Operation(summary="管理端-CMS-节点批量删除")
        @PostMapping(value = "/delete")
        public Result<String> deleteBatch(@RequestBody Collection<String> list) {
            this.bizConfigService.removeByIds(list);
            return Result.ok("批量删除成功!");
        }

        @Operation(summary="管理端-CMS-节点通过id查询")
        @GetMapping(value = "/queryById")
        public Result<BizConfig> queryById(@RequestParam(name="id") String id) {
            BizConfig bizConfig = bizConfigService.getById(id);
            return Result.ok(bizConfig);
        }

}