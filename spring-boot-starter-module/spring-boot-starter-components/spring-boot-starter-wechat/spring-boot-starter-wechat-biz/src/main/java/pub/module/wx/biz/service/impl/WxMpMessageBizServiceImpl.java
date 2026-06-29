package pub.module.wx.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.ai.api.dto.AiChatRequestDTO;
import pub.module.ai.api.dto.AiChatResponseDTO;
import pub.module.ai.api.service.ApiAiChatService;
import pub.module.wx.api.constants.WxMpMessageDirectionCode;
import pub.module.wx.api.constants.WxMpMessageReplySourceCode;
import pub.module.wx.api.dto.WxMpMessageReplyDTO;
import pub.module.wx.biz.config.WxMpRuntimeRefresher;
import pub.module.wx.biz.service.WxMpMessageBizService;
import pub.module.wx.crud.entity.WxMpConfig;
import pub.module.wx.crud.entity.WxMpFan;
import pub.module.wx.crud.entity.WxMpMessage;
import pub.module.wx.crud.service.WxMpConfigService;
import pub.module.wx.crud.service.WxMpFanService;
import pub.module.wx.crud.service.WxMpMessageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * 微信公众号消息业务实现。
 */
@Slf4j
@Service
public class WxMpMessageBizServiceImpl implements WxMpMessageBizService {

    @Resource
    private WxMpConfigService wxMpConfigService;
    @Resource
    private WxMpFanService wxMpFanService;
    @Resource
    private WxMpMessageService wxMpMessageService;
    @Resource
    private WxMpRuntimeRefresher wxMpRuntimeRefresher;
    @Resource
    private WxMpService wxMpService;
    @Resource
    private ObjectProvider<ApiAiChatService> apiAiChatServiceProvider;

    @Override
    public void handleInboundMessage(WxMpConfigContext context, WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        if (context == null || wxMessage == null || StrUtil.isBlank(wxMessage.getFromUser())) {
            return;
        }
        WxMpConfig config = wxMpConfigService.getByCode(context.wxMpConfigCode());
        if (config == null) {
            return;
        }
        String openId = wxMessage.getFromUser();
        String typeCode = StrUtil.blankToDefault(wxMessage.getMsgType(), "unknown");
        if (XmlMsgType.EVENT.equals(typeCode)) {
            return;
        }
        String content = extractInboundContent(wxMessage, typeCode);
        WxMpFan fan = upsertFan(config, openId, null, content);
        saveMessage(config.getWxMpConfigCode(), openId, WxMpMessageDirectionCode.IN.getCode(), typeCode,
                content, wxMessage.getMediaId(), wxMessage.getMsgId(), null, fan.getAiChatSessionCode());

        if (shouldAutoAiReply(config, typeCode, content)) {
            wxMpRuntimeRefresher.ensureLoaded();
            wxMpService.switchoverTo(config.getWxMpConfigAppId());
            triggerAiReplyAsync(config, fan, content);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyManual(WxMpMessageReplyDTO dto) {
        Assert.notNull(dto, "回复内容不能为空");
        Assert.notBlank(dto.getWxMpConfigCode(), "wx_mp_config_code 不能为空");
        Assert.notBlank(dto.getWxMpFanOpenId(), "openId 不能为空");
        Assert.notBlank(dto.getContent(), "回复内容不能为空");
        WxMpConfig config = wxMpConfigService.getByCode(dto.getWxMpConfigCode());
        Assert.notNull(config, "微信公众号配置不存在");
        wxMpRuntimeRefresher.ensureLoaded();
        wxMpService.switchoverTo(config.getWxMpConfigAppId());
        sendKefuText(wxMpService, dto.getWxMpFanOpenId(), dto.getContent());
        WxMpFan fan = upsertFan(config, dto.getWxMpFanOpenId(), null, dto.getContent());
        saveMessage(config.getWxMpConfigCode(), dto.getWxMpFanOpenId(), WxMpMessageDirectionCode.OUT.getCode(),
                XmlMsgType.TEXT, dto.getContent(), null, null,
                WxMpMessageReplySourceCode.MANUAL.getCode(), fan.getAiChatSessionCode());
    }

    @Override
    public List<WxMpFan> listConversations(String wxMpConfigCode) {
        Assert.notBlank(wxMpConfigCode, "wx_mp_config_code 不能为空");
        return wxMpFanService.listByConfigCode(wxMpConfigCode);
    }

    @Override
    public List<WxMpMessage> listMessages(String wxMpConfigCode, String openId) {
        Assert.notBlank(wxMpConfigCode, "wx_mp_config_code 不能为空");
        Assert.notBlank(openId, "openId 不能为空");
        return wxMpMessageService.listByFan(wxMpConfigCode, openId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordSubscribeEvent(String wxMpConfigCode, String openId, String nickname) {
        if (StrUtil.hasBlank(wxMpConfigCode, openId)) {
            return;
        }
        WxMpConfig config = wxMpConfigService.getByCode(wxMpConfigCode);
        if (config == null) {
            return;
        }
        String content = "[关注公众号]";
        WxMpFan fan = upsertFan(config, openId, nickname, content);
        saveMessage(config.getWxMpConfigCode(), openId, WxMpMessageDirectionCode.IN.getCode(),
                XmlMsgType.EVENT, content, null, null, null, fan.getAiChatSessionCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxMpXmlOutMessage buildSubscribeReply(WxMpConfig config, WxMpXmlMessage wxMessage) {
        if (config == null || wxMessage == null || !StatusCodeEnum.isYesValue(config.getWxMpConfigSubscribeReplyStatusCode())) {
            return null;
        }
        if (StrUtil.isBlank(config.getWxMpConfigSubscribeReplyJson())) {
            return null;
        }
        JSONObject articleJson;
        try {
            articleJson = JSONUtil.parseObj(config.getWxMpConfigSubscribeReplyJson());
        }
        catch (Exception e) {
            log.warn("关注回复图文 JSON 解析失败 wx_mp_config_code={}: {}", config.getWxMpConfigCode(), e.getMessage());
            return null;
        }
        String title = StrUtil.trim(articleJson.getStr("title"));
        String url = StrUtil.trim(articleJson.getStr("url"));
        String picUrl = StrUtil.trim(articleJson.getStr("picUrl"));
        if (StrUtil.hasBlank(title, url, picUrl)) {
            log.warn("关注回复图文配置不完整 wx_mp_config_code={}", config.getWxMpConfigCode());
            return null;
        }
        String description = StrUtil.blankToDefault(StrUtil.trim(articleJson.getStr("description")), title);
        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setPicUrl(picUrl);
        item.setUrl(url);
        String openId = wxMessage.getFromUser();
        WxMpFan fan = upsertFan(config, openId, null, "[图文] " + title);
        saveMessage(config.getWxMpConfigCode(), openId, WxMpMessageDirectionCode.OUT.getCode(),
                XmlMsgType.NEWS, title, null, null,
                WxMpMessageReplySourceCode.SUBSCRIBE_REPLY.getCode(), fan.getAiChatSessionCode());
        return WxMpXmlOutMessage.NEWS()
                .fromUser(wxMessage.getToUser())
                .toUser(openId)
                .addArticle(item)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFanNickname(String wxMpConfigCode, String openId, String nickname) {
        if (StrUtil.hasBlank(wxMpConfigCode, openId)) {
            return;
        }
        WxMpConfig config = wxMpConfigService.getByCode(wxMpConfigCode);
        if (config == null) {
            return;
        }
        WxMpFan fan = wxMpFanService.getByConfigAndOpenId(wxMpConfigCode, openId);
        if (fan == null) {
            fan = new WxMpFan();
            fan.setWxMpConfigCode(wxMpConfigCode);
            fan.setWxMpFanOpenId(openId);
            fan.setWxMpFanNickname(nickname);
            wxMpFanService.save(fan);
            return;
        }
        if (StrUtil.isNotBlank(nickname) && !nickname.equals(fan.getWxMpFanNickname())) {
            fan.setWxMpFanNickname(nickname);
            wxMpFanService.updateById(fan);
        }
    }

    private boolean shouldAutoAiReply(WxMpConfig config, String typeCode, String content) {
        if (!StatusCodeEnum.isYesValue(config.getWxMpConfigAiAutoReplyStatusCode())) {
            return false;
        }
        if (StrUtil.isBlank(config.getWxMpConfigAiAgentCode())) {
            return false;
        }
        if (!XmlMsgType.TEXT.equals(typeCode)) {
            return false;
        }
        return StrUtil.isNotBlank(content);
    }

    private void triggerAiReplyAsync(WxMpConfig config, WxMpFan fan, String userMessage) {
        ApiAiChatService aiChatService = apiAiChatServiceProvider.getIfAvailable();
        if (aiChatService == null) {
            log.warn("AI 模块未加载，跳过公众号自动回复 wx_mp_config_code={}", config.getWxMpConfigCode());
            return;
        }
        String appId = config.getWxMpConfigAppId();
        CompletableFuture.runAsync(() -> {
            try {
                wxMpRuntimeRefresher.ensureLoaded();
                wxMpService.switchoverTo(appId);
                AiChatRequestDTO req = new AiChatRequestDTO();
                req.setAiAgentCode(config.getWxMpConfigAiAgentCode());
                req.setUserCode(buildAiUserCode(config.getWxMpConfigCode(), fan.getWxMpFanOpenId()));
                req.setAiChatSessionCode(fan.getAiChatSessionCode());
                req.setMessage(userMessage);
                AiChatResponseDTO resp = aiChatService.chat(req);
                if (resp == null || StrUtil.isBlank(resp.getReply())) {
                    return;
                }
                if (StrUtil.isNotBlank(resp.getAiChatSessionCode())
                        && !resp.getAiChatSessionCode().equals(fan.getAiChatSessionCode())) {
                    fan.setAiChatSessionCode(resp.getAiChatSessionCode());
                    wxMpFanService.updateById(fan);
                }
                sendKefuText(wxMpService, fan.getWxMpFanOpenId(), resp.getReply());
                upsertFan(config, fan.getWxMpFanOpenId(), fan.getWxMpFanNickname(), resp.getReply());
                saveMessage(config.getWxMpConfigCode(), fan.getWxMpFanOpenId(), WxMpMessageDirectionCode.OUT.getCode(),
                        XmlMsgType.TEXT, resp.getReply(), null, null,
                        WxMpMessageReplySourceCode.AUTO_AI.getCode(), resp.getAiChatSessionCode());
            }
            catch (Exception e) {
                log.error("公众号 AI 自动回复失败 wx_mp_config_code={} openId={}: {}",
                        config.getWxMpConfigCode(), fan.getWxMpFanOpenId(), e.getMessage(), e);
            }
        });
    }

    private WxMpFan upsertFan(WxMpConfig config, String openId, String nickname, String lastContent) {
        WxMpFan fan = wxMpFanService.getByConfigAndOpenId(config.getWxMpConfigCode(), openId);
        LocalDateTime now = LocalDateTime.now();
        if (fan == null) {
            fan = new WxMpFan();
            fan.setWxMpConfigCode(config.getWxMpConfigCode());
            fan.setWxMpFanOpenId(openId);
            fan.setWxMpFanNickname(nickname);
            fan.setWxMpFanLastMessageContent(abbreviate(lastContent));
            fan.setWxMpFanLastMessageTime(now);
            wxMpFanService.save(fan);
            return fan;
        }
        if (StrUtil.isNotBlank(nickname)) {
            fan.setWxMpFanNickname(nickname);
        }
        fan.setWxMpFanLastMessageContent(abbreviate(lastContent));
        fan.setWxMpFanLastMessageTime(now);
        wxMpFanService.updateById(fan);
        return fan;
    }

    private void saveMessage(String configCode, String openId, String direction, String typeCode, String content,
                             String mediaId, Long wxMsgId, String replySource, String aiSessionCode) {
        WxMpMessage message = new WxMpMessage();
        message.setWxMpConfigCode(configCode);
        message.setWxMpFanOpenId(openId);
        message.setWxMpMessageDirectionCode(direction);
        message.setWxMpMessageTypeCode(typeCode);
        message.setWxMpMessageContent(content);
        message.setWxMpMessageMediaId(mediaId);
        if (wxMsgId != null) {
            message.setWxMpMessageWxMsgId(String.valueOf(wxMsgId));
        }
        message.setWxMpMessageReplySourceCode(replySource);
        message.setAiChatSessionCode(aiSessionCode);
        wxMpMessageService.save(message);
    }

    private String extractInboundContent(WxMpXmlMessage wxMessage, String typeCode) {
        if (XmlMsgType.TEXT.equals(typeCode)) {
            return StrUtil.trim(wxMessage.getContent());
        }
        if (XmlMsgType.IMAGE.equals(typeCode)) {
            return "[图片]";
        }
        if (XmlMsgType.VOICE.equals(typeCode)) {
            if (StrUtil.isNotBlank(wxMessage.getRecognition())) {
                return wxMessage.getRecognition();
            }
            return "[语音]";
        }
        if (XmlMsgType.VIDEO.equals(typeCode) || XmlMsgType.SHORTVIDEO.equals(typeCode)) {
            return "[视频]";
        }
        if (XmlMsgType.LOCATION.equals(typeCode)) {
            return "[位置] " + StrUtil.blankToDefault(wxMessage.getLabel(), "");
        }
        if (XmlMsgType.LINK.equals(typeCode)) {
            return StrUtil.format("[链接] {}", StrUtil.blankToDefault(wxMessage.getTitle(), wxMessage.getUrl()));
        }
        return "[" + typeCode + "]";
    }

    private void sendKefuText(WxMpService wxMpService, String openId, String content) {
        try {
            wxMpService.getKefuService().sendKefuMessage(
                    WxMpKefuMessage.TEXT().toUser(openId).content(content).build());
        }
        catch (WxErrorException e) {
            throw new IllegalArgumentException("发送客服消息失败：" + e.getError().getErrorMsg());
        }
    }

    private static String buildAiUserCode(String configCode, String openId) {
        return "wx_mp:" + configCode + ":" + openId;
    }

    private static String abbreviate(String content) {
        if (content == null) {
            return null;
        }
        String t = content.trim();
        return t.length() <= 200 ? t : t.substring(0, 200) + "...";
    }
}
