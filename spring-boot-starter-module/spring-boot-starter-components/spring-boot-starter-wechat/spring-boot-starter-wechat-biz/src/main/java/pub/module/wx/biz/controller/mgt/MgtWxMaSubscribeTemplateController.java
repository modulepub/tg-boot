package pub.module.wx.biz.controller.mgt;

import cn.hutool.core.util.StrUtil;
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
import pub.module.wx.api.dto.WxMaSubscribeTemplateDTO;
import pub.module.wx.api.dto.WxMaSubscribeTemplateFieldDTO;
import pub.module.wx.api.dto.WxMaSubscribeTemplateSendTestDTO;
import pub.module.wx.api.service.ApiWxMaSubscribeMessageService;
import pub.module.wx.api.service.ApiWxMaSubscribeTemplateService;
import pub.module.wx.biz.service.WxMaSubscribeTemplateSendTestService;
import pub.module.wx.crud.entity.WxMaSubscribeTemplate;
import pub.module.wx.crud.service.WxMaSubscribeTemplateService;

import java.util.List;

/**
 * 管理端：微信小程序订阅消息模板（仅编辑，不可新增/删除）。
 */
@Tag(name = "管理端-wx_ma_subscribe_template")
@RestController
@RequestMapping("/mgt/wx/wxMaSubscribeTemplate")
@Slf4j
public class MgtWxMaSubscribeTemplateController {

    @Resource
    private WxMaSubscribeTemplateService wxMaSubscribeTemplateService;
    @Resource
    private ApiWxMaSubscribeTemplateService apiWxMaSubscribeTemplateService;
    @Resource
    private WxMaSubscribeTemplateSendTestService wxMaSubscribeTemplateSendTestService;

    @Operation(summary = "管理端-wx_ma_subscribe_template-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<WxMaSubscribeTemplate>> queryPageList(WxMaSubscribeTemplate query,
                                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<WxMaSubscribeTemplate> queryWrapper = WebQueryUtil.buildQuery(query);
        queryWrapper.orderByAsc("wx_ma_subscribe_template_code");
        Page<WxMaSubscribeTemplate> page = new Page<>(pageNo, pageSize);
        return Result.ok(wxMaSubscribeTemplateService.page(page, queryWrapper));
    }

    @Operation(summary = "管理端-wx_ma_subscribe_template-按模板编码查询")
    @GetMapping(value = "/queryById")
    public Result<WxMaSubscribeTemplate> queryById(@RequestParam(name = "id") String id) {
        WxMaSubscribeTemplate entity = wxMaSubscribeTemplateService.getById(id);
        if (entity == null) {
            entity = wxMaSubscribeTemplateService.getByCode(id);
        }
        return Result.ok(entity);
    }

    @Operation(summary = "管理端-wx_ma_subscribe_template-编辑（不可改模板编码，不可新增）")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody WxMaSubscribeTemplateDTO dto) {
        apiWxMaSubscribeTemplateService.update(dto);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-wx_ma_subscribe_template-解析模板字段（测试发送填参）")
    @GetMapping(value = "/listFields")
    public Result<List<WxMaSubscribeTemplateFieldDTO>> listFields(
            @RequestParam(name = "wxMaSubscribeTemplateCode") String wxMaSubscribeTemplateCode) {
        return Result.ok(apiWxMaSubscribeTemplateService.listFieldsByCode(wxMaSubscribeTemplateCode));
    }

    @Operation(summary = "管理端-wx_ma_subscribe_template-测试发送")
    @PostMapping(value = "/sendTest")
    public Result<ApiWxMaSubscribeMessageService.SendResult> sendTest(
            @RequestBody WxMaSubscribeTemplateSendTestDTO dto) {
        ApiWxMaSubscribeMessageService.SendResult result = wxMaSubscribeTemplateSendTestService.sendTest(dto);
        if (result != null && result.isSuccess()) {
            return Result.ok("发送成功", result);
        }
        String errMsg = result == null ? "发送失败"
                : StrUtil.blankToDefault(result.getWxErrMsg(), "发送失败");
        if (result != null && StrUtil.isNotBlank(result.getWxErrCode())) {
            errMsg = errMsg + "（" + result.getWxErrCode() + "）";
        }
        return Result.error(errMsg);
    }
}
