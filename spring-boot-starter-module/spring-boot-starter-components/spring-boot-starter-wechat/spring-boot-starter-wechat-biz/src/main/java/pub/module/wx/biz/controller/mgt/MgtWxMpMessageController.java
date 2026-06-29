package pub.module.wx.biz.controller.mgt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.wx.api.dto.WxMpMessageReplyDTO;
import pub.module.wx.biz.service.WxMpMessageBizService;
import pub.module.wx.crud.entity.WxMpFan;
import pub.module.wx.crud.entity.WxMpMessage;

import java.util.List;

/**
 * 管理端：微信公众号消息。
 */
@Tag(name = "管理端-wx_mp_message")
@RestController
@RequestMapping("/mgt/wx/wxMpMessage")
public class MgtWxMpMessageController {

    @Resource
    private WxMpMessageBizService wxMpMessageBizService;

    @Operation(summary = "管理端-公众号消息-会话列表")
    @GetMapping("/conversations")
    public Result<List<WxMpFan>> conversations(@RequestParam(name = "wxMpConfigCode") String wxMpConfigCode) {
        return Result.ok(wxMpMessageBizService.listConversations(wxMpConfigCode));
    }

    @Operation(summary = "管理端-公众号消息-消息记录")
    @GetMapping("/messages")
    public Result<List<WxMpMessage>> messages(@RequestParam(name = "wxMpConfigCode") String wxMpConfigCode,
                                              @RequestParam(name = "openId") String openId) {
        return Result.ok(wxMpMessageBizService.listMessages(wxMpConfigCode, openId));
    }

    @Operation(summary = "管理端-公众号消息-人工回复")
    @PostMapping("/reply")
    public Result<String> reply(@RequestBody WxMpMessageReplyDTO dto) {
        wxMpMessageBizService.replyManual(dto);
        return Result.ok("回复成功");
    }
}
