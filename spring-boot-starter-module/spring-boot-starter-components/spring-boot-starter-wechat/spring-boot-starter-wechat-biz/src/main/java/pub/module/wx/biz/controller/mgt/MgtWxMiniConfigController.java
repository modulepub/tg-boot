package pub.module.wx.biz.controller.mgt;

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
import pub.module.wx.api.dto.WxMiniConfigDTO;
import pub.module.wx.api.service.ApiWxMiniConfigService;
import pub.module.wx.crud.entity.WxMiniConfig;
import pub.module.wx.crud.service.WxMiniConfigService;

import java.util.Collection;

/**
 * 管理端：微信小程序配置 wx_mini_config。
 */
@Tag(name = "管理端-wx_mini_config")
@RestController
@RequestMapping("/mgt/wx/wxMiniConfig")
@Slf4j
public class MgtWxMiniConfigController {

    @Resource
    private WxMiniConfigService wxMiniConfigService;
    @Resource
    private ApiWxMiniConfigService apiWxMiniConfigService;

    @Operation(summary = "管理端-wx_mini_config-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<WxMiniConfig>> queryPageList(WxMiniConfig wxMiniConfig,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<WxMiniConfig> queryWrapper = WebQueryUtil.buildQuery(wxMiniConfig);
        Page<WxMiniConfig> page = new Page<>(pageNo, pageSize);
        IPage<WxMiniConfig> pageList = wxMiniConfigService.page(page, queryWrapper);
        pageList.getRecords().forEach(this::maskSecret);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-wx_mini_config-新增（并刷新 WxMa 运行时配置）")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody WxMiniConfigDTO dto) {
        apiWxMiniConfigService.addAndRefreshRuntime(dto);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-wx_mini_config-编辑（并刷新 WxMa 运行时配置）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody WxMiniConfigDTO dto) {
        apiWxMiniConfigService.updateAndRefreshRuntime(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-wx_mini_config-批量删除（并刷新 WxMa 运行时配置）")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        apiWxMiniConfigService.removeAndRefreshRuntime(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-wx_mini_config-按主键或配置编码查询")
    @GetMapping(value = "/queryById")
    public Result<WxMiniConfig> queryById(@RequestParam(name = "id") String id) {
        WxMiniConfig entity = wxMiniConfigService.getById(id);
        if (entity == null) {
            entity = wxMiniConfigService.getByCode(id);
        }
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-wx_mini_config-仅从数据库刷新 WxMa 运行时配置")
    @PostMapping(value = "/refreshRuntime")
    public Result<String> refreshRuntime() {
        apiWxMiniConfigService.refreshWxMaRuntimeFromDatabase();
        return Result.ok("已刷新微信小程序运行时配置");
    }

    private void maskSecret(WxMiniConfig row) {
        if (row == null || row.getWxMiniConfigAppSecret() == null) {
            return;
        }
        String secret = row.getWxMiniConfigAppSecret();
        if (secret.length() <= 8) {
            row.setWxMiniConfigAppSecret("****");
            return;
        }
        row.setWxMiniConfigAppSecret(secret.substring(0, 4) + "****" + secret.substring(secret.length() - 4));
    }
}
