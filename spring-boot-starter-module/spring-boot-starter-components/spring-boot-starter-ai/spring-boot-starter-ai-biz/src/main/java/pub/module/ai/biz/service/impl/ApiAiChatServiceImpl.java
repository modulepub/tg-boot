package pub.module.ai.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.ai.api.constants.AiChatMessageRoleCode;
import pub.module.ai.api.constants.AiProviderCode;
import pub.module.ai.api.dto.AiChatMessageDTO;
import pub.module.ai.api.dto.AiChatRequestDTO;
import pub.module.ai.api.dto.AiChatResponseDTO;
import pub.module.ai.api.service.ApiAiChatService;
import pub.module.ai.biz.exception.AiChatException;
import pub.module.ai.biz.service.AiProviderRegistry;
import pub.module.ai.biz.service.SpiAiChatService;
import pub.module.ai.crud.entity.*;
import pub.module.ai.crud.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiAiChatServiceImpl implements ApiAiChatService {

    private static final String DEFAULT_PROVIDER = AiProviderCode.OPENAI.getCode();

    @Resource
    private IAiAgentService aiAgentService;
    @Resource
    private IAiApiConfigService aiApiConfigService;
    @Resource
    private IAiChatSessionService aiChatSessionService;
    @Resource
    private IAiChatMessageService aiChatMessageService;
    @Resource
    private IAiUsageRecordService aiUsageRecordService;
    @Resource
    private AiProviderRegistry aiProviderRegistry;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiChatResponseDTO chat(AiChatRequestDTO request) {
        Assert.notNull(request, "请求不能为空");
        Assert.notBlank(request.getAiAgentCode(), "aiAgentCode 不能为空");
        Assert.notBlank(request.getUserCode(), "userCode 不能为空");
        Assert.notBlank(request.getMessage(), "message 不能为空");

        AiAgent agent = aiAgentService.getByCode(request.getAiAgentCode());
        Assert.notNull(agent, "智能体不存在：" + request.getAiAgentCode());
        Assert.isTrue("1".equals(agent.getAiAgentEnabledCode()), "智能体未启用：" + request.getAiAgentCode());

        AiApiConfig apiConfig = resolveApiConfig(agent);
        Assert.notNull(apiConfig, "未找到可用的 AI 接口配置，请先配置并启用至少一条接口");

        String model = StrUtil.isNotBlank(agent.getAiAgentModel())
                ? agent.getAiAgentModel()
                : apiConfig.getAiApiConfigDefaultModel();
        Assert.notBlank(model, "模型名称未配置");

        AiChatSession session = resolveSession(request, agent);

        saveMessage(session.getAiChatSessionCode(), AiChatMessageRoleCode.USER.getCode(), request.getMessage());

        List<AiChatMessage> history = aiChatMessageService.listBySessionCode(session.getAiChatSessionCode());
        List<SpiAiChatService.SpiChatMessage> spiMessages = buildSpiMessages(agent, history);

        String provider = StrUtil.isNotBlank(apiConfig.getAiProviderCode())
                ? apiConfig.getAiProviderCode()
                : DEFAULT_PROVIDER;
        SpiAiChatService spi = aiProviderRegistry.requireOrDefault(provider, DEFAULT_PROVIDER);

        SpiAiChatService.SpiChatDTO spiDto = SpiAiChatService.SpiChatDTO.builder()
                .baseUrl(apiConfig.getAiApiConfigBaseUrl())
                .apiKey(apiConfig.getAiApiConfigApiKey())
                .model(model)
                .messages(spiMessages)
                .build();

        AiUsageRecord usageRecord = new AiUsageRecord();
        usageRecord.setUserCode(request.getUserCode());
        usageRecord.setAiAgentCode(agent.getAiAgentCode());
        usageRecord.setAiApiConfigCode(apiConfig.getAiApiConfigCode());
        usageRecord.setAiChatSessionCode(session.getAiChatSessionCode());
        usageRecord.setAiUsageRecordModel(model);
        usageRecord.setAiUsageRecordInputUnitPrice(defaultPrice(apiConfig.getAiApiConfigInputPricePer1k()));
        usageRecord.setAiUsageRecordOutputUnitPrice(defaultPrice(apiConfig.getAiApiConfigOutputPricePer1k()));

        try {
            SpiAiChatService.SpiChatResult result = spi.spiChat(spiDto);

            saveMessage(session.getAiChatSessionCode(), AiChatMessageRoleCode.ASSISTANT.getCode(), result.getReply());

            usageRecord.setAiUsageRecordPromptTokens(result.getPromptTokens());
            usageRecord.setAiUsageRecordCompletionTokens(result.getCompletionTokens());
            usageRecord.setAiUsageRecordTotalTokens(result.getTotalTokens());
            usageRecord.setAiUsageRecordTotalPrice(calculatePrice(
                    result.getPromptTokens(), result.getCompletionTokens(),
                    usageRecord.getAiUsageRecordInputUnitPrice(), usageRecord.getAiUsageRecordOutputUnitPrice()));
            usageRecord.setAiUsageRecordSuccessCode("1");
            aiUsageRecordService.save(usageRecord);

            AiChatResponseDTO response = new AiChatResponseDTO();
            response.setAiChatSessionCode(session.getAiChatSessionCode());
            response.setReply(result.getReply());
            response.setModel(result.getModel());
            response.setPromptTokens(result.getPromptTokens());
            response.setCompletionTokens(result.getCompletionTokens());
            response.setTotalTokens(result.getTotalTokens());
            response.setTotalPrice(usageRecord.getAiUsageRecordTotalPrice());
            response.setAiUsageRecordCode(usageRecord.getAiUsageRecordCode());
            response.setMessages(toMessageDtos(aiChatMessageService.listBySessionCode(session.getAiChatSessionCode())));
            return response;
        } catch (AiChatException e) {
            usageRecord.setAiUsageRecordSuccessCode("0");
            usageRecord.setAiUsageRecordErrorMessage(e.getMessage());
            usageRecord.setAiUsageRecordTotalPrice(BigDecimal.ZERO);
            aiUsageRecordService.save(usageRecord);
            throw e;
        } catch (Exception e) {
            usageRecord.setAiUsageRecordSuccessCode("0");
            usageRecord.setAiUsageRecordErrorMessage(e.getMessage());
            usageRecord.setAiUsageRecordTotalPrice(BigDecimal.ZERO);
            aiUsageRecordService.save(usageRecord);
            throw new AiChatException(e.getMessage());
        }
    }

    private AiApiConfig resolveApiConfig(AiAgent agent) {
        if (StrUtil.isNotBlank(agent.getAiApiConfigCode())) {
            AiApiConfig config = aiApiConfigService.getByCode(agent.getAiApiConfigCode());
            if (config != null && "1".equals(config.getAiApiConfigEnabledCode())) {
                return config;
            }
            Assert.notNull(config, "AI 接口配置不存在：" + agent.getAiApiConfigCode());
            throw new AiChatException("AI 接口配置未启用：" + agent.getAiApiConfigCode());
        }
        return aiApiConfigService.getFirstEnabled();
    }

    private AiChatSession resolveSession(AiChatRequestDTO request, AiAgent agent) {
        if (StrUtil.isNotBlank(request.getAiChatSessionCode())) {
            AiChatSession existing = aiChatSessionService.getByCode(request.getAiChatSessionCode());
            Assert.notNull(existing, "会话不存在：" + request.getAiChatSessionCode());
            Assert.isTrue(request.getUserCode().equals(existing.getUserCode()), "会话与用户不匹配");
            Assert.isTrue(agent.getAiAgentCode().equals(existing.getAiAgentCode()), "会话与智能体不匹配");
            return existing;
        }
        AiChatSession session = new AiChatSession();
        session.setUserCode(request.getUserCode());
        session.setAiAgentCode(agent.getAiAgentCode());
        String title = request.getMessage().length() > 50
                ? request.getMessage().substring(0, 50) + "..."
                : request.getMessage();
        session.setAiChatSessionTitle(title);
        aiChatSessionService.save(session);
        return session;
    }

    private void saveMessage(String sessionCode, String role, String content) {
        AiChatMessage message = new AiChatMessage();
        message.setAiChatSessionCode(sessionCode);
        message.setAiChatMessageRoleCode(role);
        message.setAiChatMessageContent(content);
        aiChatMessageService.save(message);
    }

    private List<SpiAiChatService.SpiChatMessage> buildSpiMessages(AiAgent agent, List<AiChatMessage> history) {
        List<SpiAiChatService.SpiChatMessage> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(agent.getAiAgentPersona())) {
            messages.add(SpiAiChatService.SpiChatMessage.builder()
                    .role(AiChatMessageRoleCode.SYSTEM.getCode())
                    .content(agent.getAiAgentPersona())
                    .build());
        }
        for (AiChatMessage msg : history) {
            messages.add(SpiAiChatService.SpiChatMessage.builder()
                    .role(msg.getAiChatMessageRoleCode())
                    .content(msg.getAiChatMessageContent())
                    .build());
        }
        return messages;
    }

    private BigDecimal defaultPrice(BigDecimal price) {
        return price != null ? price : BigDecimal.ZERO;
    }

    private BigDecimal calculatePrice(Integer promptTokens, Integer completionTokens,
                                      BigDecimal inputUnitPrice, BigDecimal outputUnitPrice) {
        BigDecimal inputCost = BigDecimal.ZERO;
        BigDecimal outputCost = BigDecimal.ZERO;
        if (promptTokens != null && inputUnitPrice != null) {
            inputCost = inputUnitPrice.multiply(BigDecimal.valueOf(promptTokens))
                    .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
        }
        if (completionTokens != null && outputUnitPrice != null) {
            outputCost = outputUnitPrice.multiply(BigDecimal.valueOf(completionTokens))
                    .divide(BigDecimal.valueOf(1000), 8, RoundingMode.HALF_UP);
        }
        return inputCost.add(outputCost).setScale(6, RoundingMode.HALF_UP);
    }

    private List<AiChatMessageDTO> toMessageDtos(List<AiChatMessage> messages) {
        return messages.stream().map(m -> {
            AiChatMessageDTO dto = new AiChatMessageDTO();
            BeanUtil.copyProperties(m, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
