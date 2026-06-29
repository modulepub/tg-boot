package pub.module.im.biz.controller.mgt;

import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.service.ApiDtContactService;
import pub.module.im.api.constants.ImSpecialUserConstants;
import pub.module.im.api.constants.ImSpecialUserConstants;
import pub.module.im.api.service.ApiImMessageService;
import pub.module.im.api.service.dto.ImConversationDTO;
import pub.module.im.api.service.dto.ImMessageDTO;
import pub.module.im.api.service.dto.ImMessageReadDTO;
import pub.module.im.api.service.dto.ImMessageSendDTO;

import java.util.List;

@Tag(name = "管理端-IM消息")
@RestController
@RequestMapping("/mgt/im/message")
public class MgtImMessageController {

    @Resource
    private ApiImMessageService apiImMessageService;
    @Resource
    private ApiDtContactService apiDtContactService;

    @Operation(summary = "管理端-会话列表（系统账号）")
    @GetMapping("/conversation/list")
    public Result<List<ImConversationDTO>> listConversations() {
        String system = ImSpecialUserConstants.MGT_SYSTEM_USER_CODE;
        List<ImConversationDTO> list = (List<ImConversationDTO>) apiImMessageService.listConversations(system);
        return Result.ok(list);
    }

    @Operation(summary = "管理端-未读消息总数（系统账号）")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        String system = ImSpecialUserConstants.MGT_SYSTEM_USER_CODE;
        return Result.ok(apiImMessageService.getUnreadCount(system));
    }

    @Operation(summary = "管理端-标记已读（系统账号）")
    @PostMapping("/read")
    public Result<String> markRead(@RequestBody ImMessageReadDTO dto) {
        String system = ImSpecialUserConstants.MGT_SYSTEM_USER_CODE;
        apiImMessageService.markRead(dto.getMessageCodes(), system);
        return Result.ok("已读成功");
    }

    @Operation(summary = "管理端-消息列表")
    @GetMapping("/list")
    public Result<List<ImMessageDTO>> list(
            @RequestParam(value = "pageNo", defaultValue = "1") long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
            @RequestParam(value = "conversationCode", required = false) String conversationCode) {
        if (conversationCode != null && !conversationCode.isBlank()) {
            return Result.ok(apiImMessageService.listMessages(conversationCode, pageNo, pageSize));
        }
        return Result.ok(List.of());
    }

    @Operation(summary = "管理端-给指定用户发消息")
    @PostMapping("/send")
    public Result<ImMessageDTO> sendMessage(@RequestBody ImMessageSendDTO dto) {
        Assert.notNull(dto, "sendDTO is null");
        Assert.notBlank(dto.getToUserCode(), "接收人不能为空");
        String system = ImSpecialUserConstants.MGT_SYSTEM_USER_CODE;
        Assert.isTrue(apiDtContactService.isMutualContact(system, dto.getToUserCode().trim()), "请先添加好友");
        return Result.ok(apiImMessageService.sendMessage(system, dto));
    }

    @Operation(summary = "管理端-获取指定用户的全部聊天记录")
    @GetMapping("/listByUser")
    public Result<List<ImMessageDTO>> listByUser(
            @RequestParam("userCode") String userCode,
            @RequestParam(value = "pageNo", defaultValue = "1") long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "50") long pageSize) {
        return Result.ok(apiImMessageService.listMessagesByUserForMgt(userCode, pageNo, pageSize));
    }
}
