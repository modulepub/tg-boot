package pub.module.wx.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.wx.api.dto.WxMaSubscribeTemplateOptionDTO;
import pub.module.wx.api.service.ApiWxMaSubscribeSendLogService;
import pub.module.wx.crud.entity.WxMaSubscribeSendLog;
import pub.module.wx.crud.service.WxMaSubscribeSendLogService;

import java.util.List;

/**
 * 管理端-微信小程序订阅消息发送记录
 */
@Tag(name = "管理端-微信订阅消息发送记录")
@RestController
@RequestMapping("/mgt/wx/wxMaSubscribeSendLog")
@Slf4j
public class MgtWxMaSubscribeSendLogController {

    @Resource
    private WxMaSubscribeSendLogService wxMaSubscribeSendLogService;
    @Resource
    private ApiWxMaSubscribeSendLogService apiWxMaSubscribeSendLogService;

    @Operation(summary = "管理端-订阅消息发送记录-分页列表")
    @GetMapping("/list")
    public Result<IPage<WxMaSubscribeSendLog>> list(WxMaSubscribeSendLog query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<WxMaSubscribeSendLog> wrapper = WebQueryUtil.buildQuery(query);
        wrapper.eq("deleted", 0).orderByDesc("create_time");
        Page<WxMaSubscribeSendLog> page = new Page<>(pageNo, pageSize);
        return Result.ok(wxMaSubscribeSendLogService.page(page, wrapper));
    }

    @Operation(summary = "管理端-订阅消息发送记录-详情")
    @GetMapping("/queryById")
    public Result<WxMaSubscribeSendLog> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(wxMaSubscribeSendLogService.getById(id));
    }

    @Operation(summary = "管理端-订阅消息模板筛选项（发送记录 group by template_id）")
    @GetMapping("/listTemplateOptions")
    public Result<List<WxMaSubscribeTemplateOptionDTO>> listTemplateOptions() {
        return Result.ok(apiWxMaSubscribeSendLogService.listTemplateOptions());
    }
}
