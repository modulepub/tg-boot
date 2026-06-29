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
import pub.module.wx.api.dto.WxPayConfigDTO;
import pub.module.wx.api.service.ApiWxPayConfigService;
import pub.module.wx.crud.entity.WxPayConfig;
import pub.module.wx.crud.service.WxPayConfigService;

import java.util.Collection;

/**
 * 管理端：微信支付配置 wx_pay_config。
 */
@Tag(name = "管理端-wx_pay_config")
@RestController
@RequestMapping("/mgt/wx/wxPayConfig")
@Slf4j
public class MgtWxPayConfigController {

    @Resource
    private WxPayConfigService wxPayConfigService;
    @Resource
    private ApiWxPayConfigService apiWxPayConfigService;

    @Operation(summary = "管理端-wx_pay_config-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<WxPayConfig>> queryPageList(WxPayConfig wxPayConfig,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<WxPayConfig> queryWrapper = WebQueryUtil.buildQuery(wxPayConfig);
        Page<WxPayConfig> page = new Page<>(pageNo, pageSize);
        IPage<WxPayConfig> pageList = wxPayConfigService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-wx_pay_config-新增（并刷新 WxPay 运行时配置）")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody WxPayConfigDTO dto) {
        apiWxPayConfigService.addAndRefreshRuntime(dto);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-wx_pay_config-编辑（并刷新 WxPay 运行时配置）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody WxPayConfigDTO dto) {
        apiWxPayConfigService.updateAndRefreshRuntime(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-wx_pay_config-批量删除（并刷新 WxPay 运行时配置）")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        apiWxPayConfigService.removeAndRefreshRuntime(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-wx_pay_config-按主键或配置编码查询")
    @GetMapping(value = "/queryById")
    public Result<WxPayConfig> queryById(@RequestParam(name = "id") String id) {
        WxPayConfig entity = wxPayConfigService.getById(id);
        if (entity == null) {
            entity = wxPayConfigService.getByCode(id);
        }
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-wx_pay_config-仅从数据库刷新 WxPay 运行时配置")
    @PostMapping(value = "/refreshRuntime")
    public Result<String> refreshRuntime() {
        apiWxPayConfigService.refreshWxPayRuntimeFromDatabase();
        return Result.ok("已刷新微信支付运行时配置");
    }
}
