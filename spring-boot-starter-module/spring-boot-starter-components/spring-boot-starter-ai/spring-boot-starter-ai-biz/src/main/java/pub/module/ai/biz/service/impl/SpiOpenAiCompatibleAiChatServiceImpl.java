package pub.module.ai.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import pub.module.ai.api.constants.AiProviderCode;
import pub.module.ai.biz.exception.AiChatException;
import pub.module.ai.biz.service.SpiAiChatService;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI 兼容 Chat Completions API 实现（适用于 OpenAI / DeepSeek / Moonshot 等）。
 */
@Service
public class SpiOpenAiCompatibleAiChatServiceImpl implements SpiAiChatService {

    @Override
    public String providerCode() {
        return AiProviderCode.OPENAI.getCode();
    }

    @Override
    public SpiChatResult spiChat(SpiChatDTO chatDTO) {
        Assert.notBlank(chatDTO.getBaseUrl(), "API Base URL 不能为空");
        Assert.notBlank(chatDTO.getApiKey(), "API Key 不能为空");
        Assert.notBlank(chatDTO.getModel(), "模型名称不能为空");
        Assert.notEmpty(chatDTO.getMessages(), "对话消息不能为空");

        String url = normalizeUrl(chatDTO.getBaseUrl()) + "/chat/completions";

        JSONArray messages = new JSONArray();
        for (SpiChatMessage msg : chatDTO.getMessages()) {
            JSONObject item = new JSONObject();
            item.set("role", msg.getRole());
            item.set("content", msg.getContent());
            messages.add(item);
        }

        JSONObject body = new JSONObject();
        body.set("model", chatDTO.getModel());
        body.set("messages", messages);
        String requestJson = body.toString();

        HttpResponse response;
        try {
            response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + chatDTO.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(requestJson)
                    .timeout(120_000)
                    .execute();
        } catch (Exception e) {
            throw new AiChatException("AI 接口请求失败：" + e.getMessage(), requestJson, null);
        }

        String responseJson = response.body();
        if (!response.isOk()) {
            throw new AiChatException("AI 接口返回错误 HTTP " + response.getStatus() + "：" + responseJson,
                    requestJson, responseJson);
        }

        JSONObject respObj = JSONUtil.parseObj(responseJson);
        JSONArray choices = respObj.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new AiChatException("AI 接口返回无 choices", requestJson, responseJson);
        }

        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        String reply = message != null ? message.getStr("content") : null;

        JSONObject usage = respObj.getJSONObject("usage");
        Integer promptTokens = usage != null ? usage.getInt("prompt_tokens") : null;
        Integer completionTokens = usage != null ? usage.getInt("completion_tokens") : null;
        Integer totalTokens = usage != null ? usage.getInt("total_tokens") : null;
        String model = respObj.getStr("model", chatDTO.getModel());

        return SpiChatResult.builder()
                .reply(reply)
                .model(model)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(totalTokens)
                .requestJson(requestJson)
                .responseJson(responseJson)
                .build();
    }

    private String normalizeUrl(String baseUrl) {
        String url = StrUtil.removeSuffix(baseUrl.trim(), "/");
        if (url.endsWith("/chat/completions")) {
            return StrUtil.removeSuffix(url, "/chat/completions");
        }
        return url;
    }
}
