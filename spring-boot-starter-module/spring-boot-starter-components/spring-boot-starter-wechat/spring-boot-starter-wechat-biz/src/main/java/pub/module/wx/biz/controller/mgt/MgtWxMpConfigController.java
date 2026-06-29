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
import pub.module.wx.api.dto.WxMpConfigDTO;
import pub.module.wx.api.dto.WxMpMenuDTO;
import pub.module.wx.api.service.ApiWxMpConfigService;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.service.WxMpConfigService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理端：微信公众号配置 wx_mp_config。
 */
@Tag(name = "管理端-wx_mp_config")
@RestController
@RequestMapping("/mgt/wx/wxMpConfig")
@Slf4j
public class MgtWxMpConfigController {

    @Resource
    private WxMpConfigService wxMpConfigService;
    @Resource
    private ApiWxMpConfigService apiWxMpConfigService;

    @Operation(summary = "管理端-wx_mp_config-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<WxMpConfig>> queryPageList(WxMpConfig wxMpConfig,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<WxMpConfig> queryWrapper = WebQueryUtil.buildQuery(wxMpConfig);
        Page<WxMpConfig> page = new Page<>(pageNo, pageSize);
        IPage<WxMpConfig> pageList = wxMpConfigService.page(page, queryWrapper);
        pageList.getRecords().forEach(this::maskSecret);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-wx_mp_config-新增（并刷新 WxMp 运行时配置）")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody WxMpConfigDTO dto) {
        apiWxMpConfigService.addAndRefreshRuntime(dto);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-wx_mp_config-编辑（并刷新 WxMp 运行时配置）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody WxMpConfigDTO dto) {
        apiWxMpConfigService.updateAndRefreshRuntime(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-wx_mp_config-批量删除（并刷新 WxMp 运行时配置）")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        apiWxMpConfigService.removeAndRefreshRuntime(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-wx_mp_config-按主键或配置编码查询")
    @GetMapping(value = "/queryById")
    public Result<WxMpConfig> queryById(@RequestParam(name = "id") String id) {
        WxMpConfig entity = wxMpConfigService.getById(id);
        if (entity == null) {
            entity = wxMpConfigService.getByCode(id);
        }
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-wx_mp_config-仅从数据库刷新 WxMp 运行时配置")
    @PostMapping(value = "/refreshRuntime")
    public Result<String> refreshRuntime() {
        apiWxMpConfigService.refreshWxMpRuntimeFromDatabase();
        return Result.ok("已刷新微信公众号运行时配置");
    }

    @Operation(summary = "管理端-wx_mp_config-查询已保存菜单")
    @GetMapping(value = "/menu")
    public Result<Map<String, Object>> getMenu(@RequestParam(name = "wxMpConfigCode") String wxMpConfigCode) {
        WxMpConfig config = wxMpConfigService.getByCode(wxMpConfigCode);
        Map<String, Object> data = new HashMap<>(4);
        if (config != null) {
            data.put("menuJson", config.getWxMpConfigMenuJson());
            data.put("publishedTime", config.getWxMpConfigMenuPublishedTime());
        }
        return Result.ok(data);
    }

    @Operation(summary = "管理端-wx_mp_config-保存菜单到数据库")
    @PostMapping(value = "/saveMenu")
    public Result<String> saveMenu(@RequestBody WxMpMenuDTO dto) {
        apiWxMpConfigService.saveMenu(dto);
        return Result.ok("菜单已保存");
    }

    @Operation(summary = "管理端-wx_mp_config-发布菜单到微信服务器")
    @PostMapping(value = "/publishMenu")
    public Result<String> publishMenu(@RequestBody Map<String, String> body) {
        apiWxMpConfigService.publishMenu(body.get("wxMpConfigCode"));
        return Result.ok("菜单已发布到微信");
    }

    @Operation(summary = "管理端-wx_mp_config-从微信服务器拉取当前菜单")
    @GetMapping(value = "/fetchRemoteMenu")
    public Result<String> fetchRemoteMenu(@RequestParam(name = "wxMpConfigCode") String wxMpConfigCode) {
        return Result.ok(apiWxMpConfigService.fetchRemoteMenu(wxMpConfigCode));
    }

    private void maskSecret(WxMpConfig row) {
        if (row == null || row.getWxMpConfigAppSecret() == null) {
            return;
        }
        String secret = row.getWxMpConfigAppSecret();
        if (secret.length() <= 8) {
            row.setWxMpConfigAppSecret("****");
            return;
        }
        row.setWxMpConfigAppSecret(secret.substring(0, 4) + "****" + secret.substring(secret.length() - 4));
    }
}
