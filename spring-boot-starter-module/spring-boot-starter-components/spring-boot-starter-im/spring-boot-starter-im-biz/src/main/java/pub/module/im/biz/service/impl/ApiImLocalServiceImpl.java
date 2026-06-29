package pub.module.im.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pub.module.im.api.constants.ImMessageTypeCodeEnum;
import pub.module.im.api.service.ApiImFriendService;
import pub.module.im.api.service.ApiImMessageService;
import pub.module.im.api.service.ApiImService;
import pub.module.im.api.service.dto.ImAccountDTO;
import pub.module.im.api.service.dto.ImAddFriendDTO;
import pub.module.im.api.service.dto.ImMessageSendDTO;
import pub.module.im.api.service.dto.ImWebSocketMessageDTO;
import pub.module.im.crud.service.ImConversationService;
import pub.module.im.crud.service.ImUserService;

/**
 * 本地 IM 核心服务实现（WebSocket + 本地持久化）
 */
@Slf4j
@Service
public class ApiImLocalServiceImpl implements ApiImService {

    @Resource
    private ImUserService imUserService;
    @Resource
    private ApiImFriendService apiImFriendService;
    @Resource
    private ApiImMessageService apiImMessageService;
    @Resource
    private ImConversationService imConversationService;
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void saveOrUpdateAccount(ImAccountDTO accountDTO) {
        Assert.notNull(accountDTO, "accountDTO is null");
        Assert.notBlank(accountDTO.getUserCode(), "userCode is null");
        imUserService.saveOrUpdateAccount(accountDTO, null);
    }

    @Override
    public void addFriend(ImAddFriendDTO addFriendDTO) {
        apiImFriendService.addFriend(addFriendDTO);
    }

    @Override
    public void removeFriendBidirectional(String userCodeA, String userCodeB) {
        apiImFriendService.removeFriendBidirectional(userCodeA, userCodeB);
    }

    @Override
    public void clearC2cChatBidirectional(String userCodeA, String userCodeB) {
        imConversationService.clearByUserPair(userCodeA, userCodeB);
    }

    @Override
    public void notifyContactRemoved(String fromUserCode, String toUserCode) {
        if (StrUtil.isBlank(fromUserCode) || StrUtil.isBlank(toUserCode)) {
            return;
        }
        ImWebSocketMessageDTO msg = new ImWebSocketMessageDTO();
        msg.setType("contact_removed");
        msg.setFromUserCode(fromUserCode.trim());
        msg.setToUserCode(toUserCode.trim());
        msg.setTimestamp(System.currentTimeMillis());
        messagingTemplate.convertAndSendToUser(toUserCode.trim(), "/queue/notifications", msg);
    }

    @Override
    public String generateUserSig(String userCode) {
        // 兼容旧客户端：本地 IM 不再签发腾讯云 UserSig
        return "local";
    }

    @Override
    public void sendC2CTextMessage(String fromUserCode, String toUserCode, String text) {
        ImMessageSendDTO sendDTO = new ImMessageSendDTO();
        sendDTO.setToUserCode(toUserCode);
        sendDTO.setTypeCode(ImMessageTypeCodeEnum.TEXT.getCode());
        sendDTO.setContent(text);
        apiImMessageService.sendMessage(fromUserCode, sendDTO);
    }

    @Override
    public void sendC2CRichMessage(String fromUserCode, String toUserCode, String title, String text, String imageUrl, String linkUrl) {
        ImMessageSendDTO sendDTO = new ImMessageSendDTO();
        sendDTO.setToUserCode(toUserCode);
        sendDTO.setTypeCode(ImMessageTypeCodeEnum.RICH.getCode());
        sendDTO.setTitle(title);
        sendDTO.setContent(text);
        sendDTO.setImageUrl(imageUrl);
        sendDTO.setLinkUrl(linkUrl);
        apiImMessageService.sendMessage(fromUserCode, sendDTO);
    }

    @Override
    public void logoutByUserCode(String userCode) {
        Assert.notBlank(userCode, "userCode is null");
        imUserService.clearUserSigByUserCode(userCode.trim());
    }
}
