package pub.module.system.biz.controller.mgt;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.crud.entity.SysAppConfig;
import pub.module.system.crud.service.SysAppConfigService;

import java.util.Collection;

/**
 * 管理端-APP配置
 */
@Tag(name = "管理端-APP配置")
@RestController
@RequestMapping("/mgt/system/sysAppConfig")
@Slf4j
public class MgtSysAppConfigController {

    @Resource
    private SysAppConfigService sysAppConfigService;

    @Operation(summary = "管理端-APP配置分页列表")
    @GetMapping("/list")
    public Result<IPage<SysAppConfig>> queryPageList(
            SysAppConfig sysAppConfig,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysAppConfig> queryWrapper = WebQueryUtil.buildQuery(sysAppConfig);
        queryWrapper.orderByDesc("update_time");
        Page<SysAppConfig> page = new Page<>(pageNo, pageSize);
        IPage<SysAppConfig> pageList = sysAppConfigService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-APP配置详情")
    @GetMapping("/queryById")
    public Result<SysAppConfig> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(sysAppConfigService.getById(id));
    }

    @Operation(summary = "管理端-APP配置新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody SysAppConfig sysAppConfig) {
        validateAndPrepare(sysAppConfig, true);
        sysAppConfigService.save(sysAppConfig);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-APP配置编辑")
    @PostMapping("/edit")
    public Result<String> edit(@RequestBody SysAppConfig sysAppConfig) {
        validateAndPrepare(sysAppConfig, false);
        sysAppConfigService.updateById(sysAppConfig);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-APP配置批量删除")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        sysAppConfigService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    private void validateAndPrepare(SysAppConfig sysAppConfig, boolean isAdd) {
        if (StrUtil.isBlank(sysAppConfig.getAppConfigKey())) {
            throw new IllegalArgumentException("配置 key 不能为空");
        }
        String appConfigKey = sysAppConfig.getAppConfigKey().trim();
        sysAppConfig.setAppConfigKey(appConfigKey);
        sysAppConfig.setAppConfigValue(sysAppConfigService.normalizeAppConfigValue(sysAppConfig.getAppConfigValue()));

        SysAppConfig existing = sysAppConfigService.getByAppConfigKey(appConfigKey);
        if (isAdd) {
            if (existing != null) {
                throw new IllegalArgumentException("配置 key 已存在: " + appConfigKey);
            }
            return;
        }
        if (StrUtil.isBlank(sysAppConfig.getId())) {
            throw new IllegalArgumentException("配置 id 不能为空");
        }
        if (existing != null && !existing.getId().equals(sysAppConfig.getId())) {
            throw new IllegalArgumentException("配置 key 已存在: " + appConfigKey);
        }
    }
}
