package pub.module.im.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.im.api.constants.ImMessageReadStatusCodeEnum;
import pub.module.im.api.constants.ImMessageTypeCodeEnum;
import pub.module.im.api.service.ApiImMessageService;
import pub.module.im.api.service.dto.ImConversationDTO;
import pub.module.im.api.service.dto.ImMessageDTO;
import pub.module.im.api.service.dto.ImMessageSendDTO;
import pub.module.im.biz.messaging.ImMessageEventPublisher;
import pub.module.im.crud.entity.ImConversation;
import pub.module.im.crud.entity.ImMessage;
import pub.module.im.crud.service.ImConversationService;
import pub.module.im.crud.service.ImMessageService;
import pub.module.im.crud.service.ImUserService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class ApiImMessageServiceImpl implements ApiImMessageService {

    @Resource
    private ImMessageService imMessageService;
    @Resource
    private ImConversationService imConversationService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private ImMessageEventPublisher imMessageEventPublisher;
    @Resource
    private ImUserService imUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImMessageDTO sendMessage(String fromUserCode, ImMessageSendDTO sendDTO) {
        Assert.notBlank(fromUserCode, "fromUserCode is null");
        Assert.notNull(sendDTO, "sendDTO is null");
        Assert.notBlank(sendDTO.getToUserCode(), "toUserCode is null");
        String to = sendDTO.getToUserCode().trim();
        String from = fromUserCode.trim();
        Assert.isTrue(!from.equals(to), "不能给自己发消息");

        String typeCode = sendDTO.getTypeCode();
        Assert.notBlank(typeCode, "typeCode is null");
        Assert.isTrue(ImMessageTypeCodeEnum.TEXT.getCode().equals(typeCode) || ImMessageTypeCodeEnum.RICH.getCode().equals(typeCode),
                "不支持的消息类型: " + typeCode);

        // 创建或获取会话
        String conversationCode = getOrCreateConversation(from, to);

        // 保存消息
        ImMessage message = new ImMessage();
        message.setImMessageCode(IdUtil.getSnowflakeNextIdStr());
        message.setImMessageConversationCode(conversationCode);
        message.setImMessageFromUserCode(from);
        message.setImMessageToUserCode(to);
        message.setImMessageTypeCode(typeCode);
        message.setImMessageReadStatusCode(ImMessageReadStatusCodeEnum.UNREAD.getCode());
        message.setImMessageSendStatusCode("1");

        if (ImMessageTypeCodeEnum.TEXT.getCode().equals(typeCode)) {
            Assert.notBlank(sendDTO.getContent(), "文本内容不能为空");
            message.setImMessageContent(sendDTO.getContent().trim());
        } else {
            Assert.notBlank(sendDTO.getTitle(), "图文标题不能为空");
            Assert.notBlank(sendDTO.getImageUrl(), "图文图片不能为空");
            message.setImMessageTitle(sendDTO.getTitle().trim());
            message.setImMessageContent(StrUtil.nullToEmpty(sendDTO.getContent()));
            message.setImMessageImageUrl(sendDTO.getImageUrl().trim());
            message.setImMessageLinkUrl(StrUtil.nullToEmpty(sendDTO.getLinkUrl()));
        }

        imMessageService.save(message);

        // 更新会话最后消息和未读数
        imConversationService.updateLastMessage(conversationCode, message.getImMessageCode());
        imConversationService.incrementUnread(conversationCode, to);
        imUserService.incrementUnreadCount(to);

        ImMessageDTO dto = toDto(message);

        // 事务提交后推送 WebSocket（避免事务回滚后仍推送消息）
        imMessageEventPublisher.publishMessageSentAfterCommit(dto);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(List<String> messageCodes, String toUserCode) {
        Assert.notEmpty(messageCodes, "messageCodes is empty");
        Assert.notBlank(toUserCode, "toUserCode is null");

        // 先查询消息获取会话编码
        List<ImMessage> messages = new ArrayList<>();
        for (String code : messageCodes) {
            ImMessage msg = imMessageService.getByCode(code);
            if (msg != null && toUserCode.trim().equals(msg.getImMessageToUserCode())) {
                messages.add(msg);
            }
        }

        if (messages.isEmpty()) {
            return;
        }

        // 按会话分组统计
        java.util.Map<String, Integer> conversationCount = new java.util.HashMap<>();
        for (ImMessage msg : messages) {
            conversationCount.merge(msg.getImMessageConversationCode(), 1, Integer::sum);
        }

        // 标记已读
        imMessageService.markRead(messageCodes, toUserCode.trim());

        // 更新会话未读数
        for (java.util.Map.Entry<String, Integer> entry : conversationCount.entrySet()) {
            imConversationService.decrementUnread(entry.getKey(), toUserCode.trim(), entry.getValue());
        }

        imUserService.syncUnreadCount(toUserCode.trim());
    }

    @Override
    public List<ImMessageDTO> listMessages(String conversationCode, long pageNo, long pageSize) {
        Assert.notBlank(conversationCode, "conversationCode is null");
        IPage<ImMessage> page = imMessageService.pageByConversation(conversationCode.trim(), pageNo, pageSize);
        List<ImMessageDTO> result = new ArrayList<>();
        for (ImMessage msg : page.getRecords()) {
            result.add(toDto(msg));
        }
        return result;
    }

    @Override
    public int getUnreadCount(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return 0;
        }
        return imMessageService.countUnreadByToUser(userCode.trim());
    }

    @Override
    public List<ImMessageDTO> listUnreadMessages(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return List.of();
        }
        List<ImMessage> list = imMessageService.listUnreadByToUser(userCode.trim());
        List<ImMessageDTO> result = new ArrayList<>();
        for (ImMessage msg : list) {
            result.add(toDto(msg));
        }
        return result;
    }

    @Override
    public List<ImMessageDTO> listMessagesByUserForMgt(String userCode, long pageNo, long pageSize) {
        Assert.notBlank(userCode, "userCode is null");
        IPage<ImMessage> page = imMessageService.pageByParticipant(userCode.trim(), pageNo, pageSize, true);
        List<ImMessageDTO> result = new ArrayList<>();
        for (ImMessage msg : page.getRecords()) {
            result.add(toDto(msg));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getOrCreateConversation(String userACode, String userBCode) {
        Assert.notBlank(userACode, "userACode is null");
        Assert.notBlank(userBCode, "userBCode is null");
        String a = userACode.trim();
        String b = userBCode.trim();

        ImConversation conv = imConversationService.getByUserPair(a, b);
        if (conv != null) {
            return conv.getImConversationCode();
        }

        // 统一排序：字典序小的为A
        String userA = a;
        String userB = b;
        if (userA.compareTo(userB) > 0) {
            String temp = userA;
            userA = userB;
            userB = temp;
        }

        ImConversation newConv = new ImConversation();
        newConv.setImConversationCode(IdUtil.getSnowflakeNextIdStr());
        newConv.setImConversationUserACode(userA);
        newConv.setImConversationUserBCode(userB);
        newConv.setImConversationUnreadCountA(0);
        newConv.setImConversationUnreadCountB(0);
        imConversationService.save(newConv);
        return newConv.getImConversationCode();
    }

    @Override
    public List<ImConversationDTO> listConversations(String userCode) {
        Assert.notBlank(userCode, "userCode is null");
        String u = userCode.trim();
        List<ImConversation> convs = imConversationService.listByUserCode(u);
        List<ImConversationDTO> result = new ArrayList<>();
        for (ImConversation conv : convs) {
            ImConversationDTO dto = new ImConversationDTO();
            dto.setConversationCode(conv.getImConversationCode());

            // 确定对方用户
            String peerCode = u.equals(conv.getImConversationUserACode())
                    ? conv.getImConversationUserBCode()
                    : conv.getImConversationUserACode();
            dto.setPeerUserCode(peerCode);

            // 查询对方用户信息
            UserDTO peer = apiSysUserService.getUserByUserCode(peerCode);
            if (peer != null) {
                dto.setPeerNickName(peer.getUserNickName());
                dto.setPeerAvatar(peer.getUserAvatar());
            }

            // 未读数
            int unread = u.equals(conv.getImConversationUserACode())
                    ? conv.getImConversationUnreadCountA()
                    : conv.getImConversationUnreadCountB();
            dto.setUnreadCount(unread);

            // 最后一条消息
            String lastMessageTime = null;
            if (StrUtil.isNotBlank(conv.getImConversationLastMessageCode())) {
                ImMessage lastMsg = imMessageService.getByCode(conv.getImConversationLastMessageCode());
                if (lastMsg != null) {
                    dto.setLastMessageContent(formatLastMessagePreview(lastMsg));
                    if (lastMsg.getCreateTime() != null) {
                        lastMessageTime = lastMsg.getCreateTime().toString();
                        dto.setLastMessageTime(lastMessageTime);
                    }
                }
            }
            if (StrUtil.isNotBlank(lastMessageTime)) {
                dto.setLastActiveTime(lastMessageTime);
            } else if (conv.getUpdateTime() != null) {
                dto.setLastActiveTime(conv.getUpdateTime().toString());
            } else if (conv.getCreateTime() != null) {
                dto.setLastActiveTime(conv.getCreateTime().toString());
            }

            result.add(dto);
        }
        result.sort(Comparator.comparing(this::resolveConversationActiveMillis).reversed());
        return result;
    }

    private long resolveConversationActiveMillis(ImConversationDTO dto) {
        long active = parseDateTimeMillis(dto.getLastActiveTime());
        if (active > 0) {
            return active;
        }
        return parseDateTimeMillis(dto.getLastMessageTime());
    }

    private long parseDateTimeMillis(String value) {
        if (StrUtil.isBlank(value)) {
            return 0L;
        }
        String raw = value.trim().replace(' ', 'T');
        try {
            return java.time.Instant.parse(raw).toEpochMilli();
        } catch (Exception ignored) {
            /**/
        }
        try {
            return java.time.LocalDateTime.parse(raw).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private String formatLastMessagePreview(ImMessage message) {
        if (message == null) {
            return "";
        }
        if (ImMessageTypeCodeEnum.RICH.getCode().equals(message.getImMessageTypeCode())) {
            return "[图文]" + StrUtil.nullToEmpty(message.getImMessageTitle());
        }
        String content = StrUtil.nullToEmpty(message.getImMessageContent()).trim();
        if (!content.startsWith("{")) {
            return content;
        }
        try {
            JSONObject json = JSONUtil.parseObj(content);
            String type = json.getStr("type");
            if (StrUtil.isBlank(type)) {
                return "新消息";
            }
            return switch (type) {
                case "wx_exchange_request" -> "[交换微信]";
                case "wx_exchange_accept" -> "我的微信号";
                case "chat_image" -> "图片";
                case "rich_link" -> "图文消息";
                case "contact_removed" -> "[联系人已解除]";
                default -> "新消息";
            };
        } catch (Exception e) {
            return content;
        }
    }

    private ImMessageDTO toDto(ImMessage message) {
        ImMessageDTO dto = new ImMessageDTO();
        BeanUtil.copyProperties(message, dto);
        dto.setMessageCode(message.getImMessageCode());
        dto.setConversationCode(message.getImMessageConversationCode());
        dto.setFromUserCode(message.getImMessageFromUserCode());
        dto.setToUserCode(message.getImMessageToUserCode());
        dto.setTypeCode(message.getImMessageTypeCode());
        dto.setContent(message.getImMessageContent());
        dto.setTitle(message.getImMessageTitle());
        dto.setImageUrl(message.getImMessageImageUrl());
        dto.setLinkUrl(message.getImMessageLinkUrl());
        dto.setReadStatusCode(message.getImMessageReadStatusCode());
        dto.setSendStatusCode(message.getImMessageSendStatusCode());
        dto.setCreateTime(message.getCreateTime() != null ? message.getCreateTime().toString() : null);
        return dto;
    }
}
