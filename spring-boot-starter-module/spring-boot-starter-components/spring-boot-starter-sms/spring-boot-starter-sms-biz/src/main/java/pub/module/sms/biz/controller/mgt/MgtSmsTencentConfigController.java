package pub.module.sms.biz.controller.mgt;

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
import pub.module.sms.api.dto.SmsTencentConfigDTO;
import pub.module.sms.api.service.ApiSmsTencentConfigService;
import pub.module.sms.crud.entity.SmsTencentConfig;
import pub.module.sms.crud.service.ISmsTencentConfigService;

import java.util.Collection;

/**
 * 管理端：腾讯云短信配置 sms_tencent_config。
 */
@Tag(name = "管理端-sms_tencent_config")
@RestController
@RequestMapping("/mgt/sms/smsTencentConfig")
@Slf4j
public class MgtSmsTencentConfigController {

    @Resource
    private ISmsTencentConfigService smsTencentConfigService;
    @Resource
    private ApiSmsTencentConfigService apiSmsTencentConfigService;

    @Operation(summary = "管理端-sms_tencent_config-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SmsTencentConfig>> queryPageList(SmsTencentConfig smsTencentConfig,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SmsTencentConfig> queryWrapper = WebQueryUtil.buildQuery(smsTencentConfig);
        Page<SmsTencentConfig> page = new Page<>(pageNo, pageSize);
        IPage<SmsTencentConfig> pageList = smsTencentConfigService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-sms_tencent_config-新增（并刷新运行时配置）")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SmsTencentConfigDTO dto) {
        apiSmsTencentConfigService.addAndRefreshRuntime(dto);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-sms_tencent_config-编辑（并刷新运行时配置）")
    @RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
    public Result<String> edit(@RequestBody SmsTencentConfigDTO dto) {
        apiSmsTencentConfigService.updateAndRefreshRuntime(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-sms_tencent_config-批量删除（并刷新运行时配置）")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        apiSmsTencentConfigService.removeAndRefreshRuntime(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-sms_tencent_config-按业务编码查询")
    @GetMapping(value = "/queryById")
    public Result<SmsTencentConfig> queryById(@RequestParam(name = "id") String id) {
        SmsTencentConfig entity = smsTencentConfigService.getByCode(id);
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-sms_tencent_config-仅从数据库刷新运行时配置")
    @PostMapping(value = "/refreshRuntime")
    public Result<String> refreshRuntime() {
        apiSmsTencentConfigService.refreshRuntimeFromDatabase();
        return Result.ok("已刷新腾讯云短信运行时配置");
    }
}
