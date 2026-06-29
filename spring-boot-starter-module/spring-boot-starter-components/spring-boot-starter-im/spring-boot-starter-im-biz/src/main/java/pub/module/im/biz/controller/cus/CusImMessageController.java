package pub.module.im.biz.controller.cus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.im.api.service.ApiImMessageService;
import pub.module.im.api.service.dto.ImConversationDTO;
import pub.module.im.api.service.dto.ImMessageDTO;
import pub.module.im.api.service.dto.ImMessageReadDTO;
import pub.module.im.api.service.dto.ImMessageSendDTO;
import pub.module.system.api.util.UserUtil;

import java.util.List;

@Tag(name = "用户端-IM消息")
@RestController
@RequestMapping("/cus/im/message")
public class CusImMessageController {

    @Resource
    private ApiImMessageService apiImMessageService;

    @Operation(summary = "用户端-发送消息（HTTP备用）")
    @PostMapping("/send")
    public Result<ImMessageDTO> sendMessage(@RequestBody ImMessageSendDTO dto) {
        String fromUserCode = UserUtil.getCurrentSysUser().getUserCode();
        return Result.ok(apiImMessageService.sendMessage(fromUserCode, dto));
    }

    @Operation(summary = "用户端-标记已读")
    @PostMapping("/read")
    public Result<String> markRead(@RequestBody ImMessageReadDTO dto) {
        String toUserCode = UserUtil.getCurrentSysUser().getUserCode();
        apiImMessageService.markRead(dto.getMessageCodes(), toUserCode);
        return Result.ok("已读成功");
    }

    @Operation(summary = "用户端-会话消息列表")
    @GetMapping("/list")
    public Result<List<ImMessageDTO>> listMessages(
            @RequestParam("conversationCode") String conversationCode,
            @RequestParam(value = "pageNo", defaultValue = "1") long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") long pageSize) {
        return Result.ok(apiImMessageService.listMessages(conversationCode, pageNo, pageSize));
    }

    @Operation(summary = "用户端-未读消息总数")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        return Result.ok(apiImMessageService.getUnreadCount(userCode));
    }

    @Operation(summary = "用户端-会话列表")
    @GetMapping("/conversation/list")
    public Result<List<ImConversationDTO>> listConversations() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        List<ImConversationDTO> list = (List<ImConversationDTO>) apiImMessageService.listConversations(userCode);
        return Result.ok(list);
    }
}
