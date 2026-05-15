package pub.module.trade.biz.controller.mgt;

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
import pub.module.trade.api.dto.TdWxPayConfigDTO;
import pub.module.trade.api.service.ApiTdWxPayConfigService;
import pub.module.trade.curd.entity.TdWxPayConfig;
import pub.module.trade.curd.service.ITdWxPayConfigService;

import java.util.Collection;

/**
 * 管理端：微信支付配置 td_wx_pay_config。
 */
@Tag(name = "管理端-td_wx_pay_config")
@RestController
@RequestMapping("/mgt/trade/tdWxPayConfig")
@Slf4j
public class MgtTdWxPayConfigController {

    @Resource
    private ITdWxPayConfigService tdWxPayConfigService;
    @Resource
    private ApiTdWxPayConfigService apiTdWxPayConfigService;

    @Operation(summary = "管理端-td_wx_pay_config-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<TdWxPayConfig>> queryPageList(TdWxPayConfig tdWxPayConfig,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<TdWxPayConfig> queryWrapper = WebQueryUtil.buildQuery(tdWxPayConfig);
        Page<TdWxPayConfig> page = new Page<>(pageNo, pageSize);
        IPage<TdWxPayConfig> pageList = tdWxPayConfigService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-td_wx_pay_config-新增（并刷新 WxPay 运行时配置）")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody TdWxPayConfigDTO dto) {
        apiTdWxPayConfigService.addAndRefreshRuntime(dto);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-td_wx_pay_config-编辑（并刷新 WxPay 运行时配置）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody TdWxPayConfigDTO dto) {
        apiTdWxPayConfigService.updateAndRefreshRuntime(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-td_wx_pay_config-批量删除（并刷新 WxPay 运行时配置）")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        apiTdWxPayConfigService.removeAndRefreshRuntime(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-td_wx_pay_config-按主键查询（id 即为 wx_pay_config_code）")
    @GetMapping(value = "/queryById")
    public Result<TdWxPayConfig> queryById(@RequestParam(name = "id") String id) {
        TdWxPayConfig entity = tdWxPayConfigService.getById(id);
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-td_wx_pay_config-仅从数据库刷新 WxPay 运行时配置")
    @PostMapping(value = "/refreshRuntime")
    public Result<String> refreshRuntime() {
        apiTdWxPayConfigService.refreshWxPayRuntimeFromDatabase();
        return Result.ok("已刷新微信支付运行时配置");
    }
}
