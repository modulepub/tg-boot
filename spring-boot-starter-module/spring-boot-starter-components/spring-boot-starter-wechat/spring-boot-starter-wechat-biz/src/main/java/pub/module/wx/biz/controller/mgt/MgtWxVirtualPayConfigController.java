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
import pub.module.wx.api.dto.WxVirtualPayConfigDTO;
import pub.module.wx.api.service.ApiWxVirtualPayConfigService;
import pub.module.wx.crud.entity.WxVirtualPayConfig;
import pub.module.wx.crud.service.WxVirtualPayConfigService;

import java.util.Collection;

/**
 * 管理端：微信小程序虚拟支付配置 wx_virtual_pay_config。
 */
@Tag(name = "管理端-wx_virtual_pay_config")
@RestController
@RequestMapping("/mgt/wx/wxVirtualPayConfig")
@Slf4j
public class MgtWxVirtualPayConfigController {

    @Resource
    private WxVirtualPayConfigService wxVirtualPayConfigService;
    @Resource
    private ApiWxVirtualPayConfigService apiWxVirtualPayConfigService;

    @Operation(summary = "管理端-wx_virtual_pay_config-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<WxVirtualPayConfig>> queryPageList(WxVirtualPayConfig wxVirtualPayConfig,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<WxVirtualPayConfig> queryWrapper = WebQueryUtil.buildQuery(wxVirtualPayConfig);
        Page<WxVirtualPayConfig> page = new Page<>(pageNo, pageSize);
        IPage<WxVirtualPayConfig> pageList = wxVirtualPayConfigService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-wx_virtual_pay_config-新增（并刷新运行时配置）")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody WxVirtualPayConfigDTO dto) {
        apiWxVirtualPayConfigService.addAndRefreshRuntime(dto);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-wx_virtual_pay_config-编辑（并刷新运行时配置）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody WxVirtualPayConfigDTO dto) {
        apiWxVirtualPayConfigService.updateAndRefreshRuntime(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-wx_virtual_pay_config-批量删除（并刷新运行时配置）")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        apiWxVirtualPayConfigService.removeAndRefreshRuntime(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-wx_virtual_pay_config-按主键或配置编码查询")
    @GetMapping(value = "/queryById")
    public Result<WxVirtualPayConfig> queryById(@RequestParam(name = "id") String id) {
        WxVirtualPayConfig entity = wxVirtualPayConfigService.getById(id);
        if (entity == null) {
            entity = wxVirtualPayConfigService.getByCode(id);
        }
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-wx_virtual_pay_config-按配置编码查询（兼容低代码 queryByAppId 误传编码）")
    @GetMapping(value = "/queryByCode")
    public Result<WxVirtualPayConfig> queryByCode(@RequestParam(name = "wxVirtualPayConfigCode") String wxVirtualPayConfigCode) {
        return Result.ok(wxVirtualPayConfigService.getByCode(wxVirtualPayConfigCode));
    }

    @Operation(summary = "管理端-wx_virtual_pay_config-仅从数据库刷新运行时配置")
    @PostMapping(value = "/refreshRuntime")
    public Result<String> refreshRuntime() {
        apiWxVirtualPayConfigService.refreshWxVirtualPayRuntimeFromDatabase();
        return Result.ok("已刷新虚拟支付运行时配置");
    }
}
