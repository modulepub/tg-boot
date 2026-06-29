package pub.module.verification.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.verification.api.dto.NpConfigDTO;
import pub.module.verification.api.service.ApiNpConfigService;
import pub.module.verification.crud.entity.NpConfig;
import pub.module.verification.crud.service.NpConfigService;

import java.util.Collection;

/**
 * 管理端：二要素核验配置 vt_np_config。
 */
@Tag(name = "管理端-vt_np_config")
@RestController
@RequestMapping("/mgt/verification/vtNpConfig")
@Slf4j
public class MgtVtNpConfigController {

    @Resource
    private NpConfigService npConfigService;
    @Resource
    private ApiNpConfigService apiNpConfigService;

    @Operation(summary = "管理端-vt_np_config-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<NpConfig>> queryPageList(NpConfig npConfig,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<NpConfig> queryWrapper = WebQueryUtil.buildQuery(npConfig);
        Page<NpConfig> page = new Page<>(pageNo, pageSize);
        IPage<NpConfig> pageList = npConfigService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-vt_np_config-新增（并刷新运行时配置）")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody NpConfigDTO dto) {
        apiNpConfigService.addAndRefreshRuntime(dto);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-vt_np_config-编辑（并刷新运行时配置）")
    @RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
    public Result<String> edit(@RequestBody NpConfigDTO dto) {
        apiNpConfigService.updateAndRefreshRuntime(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-vt_np_config-批量删除（并刷新运行时配置）")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        apiNpConfigService.removeAndRefreshRuntime(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-vt_np_config-按主键查询")
    @GetMapping(value = "/queryById")
    public Result<NpConfig> queryById(@RequestParam(name = "id") String id) {
        NpConfig entity = npConfigService.getById(id);
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-vt_np_config-仅从数据库刷新运行时配置")
    @PostMapping(value = "/refreshRuntime")
    public Result<String> refreshRuntime() {
        apiNpConfigService.refreshRuntimeFromDatabase();
        return Result.ok("已刷新二要素核验运行时配置");
    }
}
