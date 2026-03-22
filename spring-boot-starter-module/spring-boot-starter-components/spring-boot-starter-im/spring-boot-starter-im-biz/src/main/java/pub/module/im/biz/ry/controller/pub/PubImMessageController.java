package pub.module.im.biz.ry.controller.pub;

import cn.hutool.json.JSONUtil;
import io.rong.messages.TxtMessage;
import io.rong.models.message.GroupMessage;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.MessageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.web.vo.Result;
import pub.module.im.biz.ry.service.BizRyService;

import jakarta.annotation.Resource;
import java.util.Collections;

/**
 * 客服坐席
 * @author tg
 * @since 2025-10-03
 * @version V1.0
 */

@Tag(name="融云即时通讯")
@RestController
@RequestMapping("/pub/im/ry/imMessage")
@Slf4j
public class PubImMessageController {
	@Resource
	private BizRyService bizRyService;

    @Data
    @Schema(description = "回调消息VO")
    public static class ReceiveVO{
        String fromUserId;
        String toUserId;
        String objectName;
        String content;
        String channelType;
        String msgTimestamp;
        String msgUID;
        String originalMsgUID;
        String sensitiveType;
        String source;
        String busChannel;
        String groupUserIds;
        String sysConfigCode;
    }


	@Operation(summary="融云-回调")
	@PostMapping(value = "/receive")
	public Result<String> add(ReceiveVO receiveVO) {
		log.info("融云回调：{}",JSONUtil.toJsonStr(receiveVO));
		return Result.ok("已收到！");
	}
    @SneakyThrows
    @Operation(summary="融云-群组消息发送")
    @PostMapping(value = "/sendGroupMessage")
    public Result<String> send(ReceiveVO receiveVO) {
        log.info("融云发送消息：{}",JSONUtil.toJsonStr(receiveVO));
            GroupMessage groupMessage = new GroupMessage();
            groupMessage.setTargetId(Collections.singletonList("0000").toArray(new String[0]));
            groupMessage.setSenderId(receiveVO.fromUserId);
            TxtMessage txtMessage = new TxtMessage(receiveVO.content, "");
            groupMessage.setObjectName(txtMessage.getType());
            groupMessage.setContent(txtMessage);
            MessageResult messageResult = bizRyService.getRongCloud().message.group.send(groupMessage);
            log.info("发送群消息返回:{}", JSONUtil.toJsonStr(messageResult));
        return Result.ok("已发送！");
    }

    @SneakyThrows
    @Operation(summary="融云-单聊消息发送")
    @PostMapping(value = "/sendUserMessage")
    public Result<String> sendUserMessage(ReceiveVO receiveVO) {
        log.info("融云发送单聊消息：{}",JSONUtil.toJsonStr(receiveVO));
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setTargetId(Collections.singletonList(receiveVO.toUserId).toArray(new String[0]));
        privateMessage.setSenderId(receiveVO.fromUserId);
        TxtMessage txtMessage = new TxtMessage(receiveVO.content, "");
        privateMessage.setObjectName(txtMessage.getType());
        privateMessage.setContent(txtMessage);
        MessageResult messageResult = bizRyService.getRongCloud().message.msgPrivate.send(privateMessage);
        log.info("发送单聊消息返回:{}", JSONUtil.toJsonStr(messageResult));
        return Result.ok("已发送！");
    }

}
