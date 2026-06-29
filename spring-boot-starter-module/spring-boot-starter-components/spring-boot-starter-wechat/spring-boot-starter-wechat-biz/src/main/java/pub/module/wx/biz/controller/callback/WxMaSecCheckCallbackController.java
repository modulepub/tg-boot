package pub.module.wx.biz.controller.callback;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.verification.api.dto.ContentModerationAsyncCallbackDTO;
import pub.module.verification.api.service.ApiContentModerationService;
import pub.module.wx.biz.util.WxMsgPushCryptoUtil;
import pub.module.wx.crud.entity.WxMiniConfig;
import pub.module.wx.crud.service.WxMiniConfigService;

import java.util.Map;

/**
 * 微信小程序「消息推送」回调：接收内容安全异步检测结果（Event=wxa_media_check）。
 *
 * <p>需在微信公众平台「开发管理 → 开发设置 → 消息推送」配置服务器地址为：
 * {@code https://域名/pub/wx/ma/sec-check/notify/{appid}}（推荐带 appid，便于多小程序区分），
 * 并与后台「小程序配置」中的 Token / EncodingAESKey / 消息格式保持一致。</p>
 *
 * <p>支持：明文模式（JSON/XML）与安全模式（AES 加密的 JSON/XML）。</p>
 */
@Slf4j
@Tag(name = "公开-微信回调")
@RestController
@RequestMapping("/pub/wx/ma")
public class WxMaSecCheckCallbackController {

    private static final String EVENT_MEDIA_CHECK = "wxa_media_check";

    @Resource
    private ApiContentModerationService apiContentModerationService;
    @Resource
    private WxMiniConfigService wxMiniConfigService;

    @Operation(summary = "微信消息推送 URL 验签（GET，配置服务器地址时调用）")
    @GetMapping({"/sec-check/notify", "/sec-check/notify/{appid}"})
    public String verify(@PathVariable(name = "appid", required = false) String appid,
                         @RequestParam(name = "signature", required = false) String signature,
                         @RequestParam(name = "timestamp", required = false) String timestamp,
                         @RequestParam(name = "nonce", required = false) String nonce,
                         @RequestParam(name = "echostr", required = false) String echostr) {
        WxMiniConfig config = resolveConfig(appid);
        if (config == null || StrUtil.isBlank(config.getWxMiniConfigToken())) {
            log.warn("微信消息推送 URL 验签失败：未找到匹配的小程序配置或未配置 Token，appid={}", appid);
            return "";
        }
        boolean ok = WxMsgPushCryptoUtil.checkSignature(config.getWxMiniConfigToken(), signature, timestamp, nonce);
        if (!ok) {
            log.warn("微信消息推送 URL 验签不通过 appid={} signature={}", config.getWxMiniConfigAppId(), signature);
            return "";
        }
        return StrUtil.nullToEmpty(echostr);
    }

    @Operation(summary = "微信小程序内容安全异步检测回调（wxa_media_check）")
    @PostMapping({"/sec-check/notify", "/sec-check/notify/{appid}"})
    public String mediaCheckNotify(@PathVariable(name = "appid", required = false) String appid,
                                   @RequestBody(required = false) String body,
                                   @RequestParam(name = "signature", required = false) String signature,
                                   @RequestParam(name = "msg_signature", required = false) String msgSignature,
                                   @RequestParam(name = "timestamp", required = false) String timestamp,
                                   @RequestParam(name = "nonce", required = false) String nonce,
                                   @RequestParam(name = "encrypt_type", required = false) String encryptType) {
        if (StrUtil.isBlank(body)) {
            return "success";
        }
        WxMiniConfig config = resolveConfig(appid);
        if (config == null) {
            log.warn("微信内容安全回调未匹配到小程序配置，appid={}，body={}", appid, StrUtil.sub(body, 0, 512));
            return "success";
        }
        try {
            String plain = resolvePlaintext(config, body, msgSignature, signature, timestamp, nonce, encryptType);
            if (StrUtil.isBlank(plain)) {
                return "success";
            }
            handlePlaintext(plain, body);
        } catch (Exception ex) {
            log.error("微信内容安全回调处理异常 appid={} body={}", config.getWxMiniConfigAppId(),
                    StrUtil.sub(body, 0, 512), ex);
        }
        return "success";
    }

    /**
     * 还原回调明文：明文模式直接返回 body；安全模式取出 Encrypt 密文并 AES 解密。
     */
    private String resolvePlaintext(WxMiniConfig config, String body, String msgSignature,
                                    String signature, String timestamp, String nonce, String encryptType) {
        String token = config.getWxMiniConfigToken();
        boolean encrypted = "aes".equalsIgnoreCase(StrUtil.trim(encryptType)) || body.contains("Encrypt");
        if (!encrypted) {
            if (StrUtil.isNotBlank(token) && StrUtil.isNotBlank(signature)
                    && !WxMsgPushCryptoUtil.checkSignature(token, signature, timestamp, nonce)) {
                log.warn("微信内容安全回调（明文）验签不通过 appid={}", config.getWxMiniConfigAppId());
                return null;
            }
            return body;
        }
        String encrypt = extractEncrypt(body);
        if (StrUtil.isBlank(encrypt)) {
            log.warn("微信内容安全回调（安全模式）未取到 Encrypt 密文 appid={}", config.getWxMiniConfigAppId());
            return null;
        }
        if (StrUtil.isNotBlank(token) && StrUtil.isNotBlank(msgSignature)
                && !WxMsgPushCryptoUtil.checkSignature(token, msgSignature, timestamp, nonce, encrypt)) {
            log.warn("微信内容安全回调（安全模式）验签不通过 appid={}", config.getWxMiniConfigAppId());
            return null;
        }
        return WxMsgPushCryptoUtil.decrypt(config.getWxMiniConfigAesKey(), encrypt);
    }

    private static String extractEncrypt(String body) {
        String trimmed = StrUtil.trim(body);
        if (StrUtil.startWith(trimmed, "{")) {
            return JSONUtil.parseObj(trimmed).getStr("Encrypt");
        }
        Map<String, Object> map = XmlUtil.xmlToMap(trimmed);
        return map != null ? str(map.get("Encrypt")) : null;
    }

    /**
     * 解析明文（JSON 或 XML），匹配 wxa_media_check 事件后回填审核结果。
     */
    private void handlePlaintext(String plain, String rawBody) {
        String trimmed = StrUtil.trim(plain);
        String event;
        String traceId;
        Integer errCode;
        String errMsg;
        String suggest;
        // wxa_media_check 回调结构：顶层 errcode/errmsg 表示检测是否成功；
        // 仅当 errcode==0 时 result.suggest 才有效（pass/review/risky）。
        if (StrUtil.startWith(trimmed, "{")) {
            JSONObject json = JSONUtil.parseObj(trimmed);
            event = json.getStr("Event");
            traceId = json.getStr("trace_id");
            errCode = json.getInt("errcode");
            errMsg = json.getStr("errmsg");
            JSONObject result = json.getJSONObject("result");
            suggest = result != null ? result.getStr("suggest") : null;
        } else {
            Map<String, Object> map = XmlUtil.xmlToMap(trimmed);
            event = str(map.get("Event"));
            traceId = str(map.get("trace_id"));
            String errCodeStr = str(map.get("errcode"));
            errCode = errCodeStr != null ? Integer.valueOf(errCodeStr) : null;
            errMsg = str(map.get("errmsg"));
            Object result = map.get("result");
            suggest = result instanceof Map ? str(((Map<?, ?>) result).get("suggest")) : null;
        }
        if (!EVENT_MEDIA_CHECK.equalsIgnoreCase(StrUtil.trim(event))) {
            return;
        }
        if (StrUtil.isBlank(traceId)) {
            log.warn("wxa_media_check 回调缺少 trace_id: {}", StrUtil.sub(rawBody, 0, 512));
            return;
        }
        ContentModerationAsyncCallbackDTO callback = new ContentModerationAsyncCallbackDTO();
        callback.setCmRecordVendorTraceId(traceId.trim());
        callback.setErrCode(errCode);
        callback.setErrMsg(errMsg);
        callback.setSuggest(suggest);
        callback.setCmRecordRemark(StrUtil.sub(plain, 0, 65535));
        apiContentModerationService.completeAsyncByTraceId(callback);
    }

    private static String str(Object value) {
        if (value == null) {
            return null;
        }
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? null : s;
    }

    private WxMiniConfig resolveConfig(String appid) {
        if (StrUtil.isNotBlank(appid)) {
            return wxMiniConfigService.lambdaQuery()
                    .eq(WxMiniConfig::getWxMiniConfigAppId, appid.trim())
                    .last("LIMIT 1")
                    .one();
        }
        return wxMiniConfigService.lambdaQuery()
                .eq(WxMiniConfig::getWxMiniConfigEnabledCode, "1")
                .orderByAsc(WxMiniConfig::getSeqNo)
                .orderByAsc(WxMiniConfig::getWxMiniConfigCode)
                .last("LIMIT 1")
                .one();
    }
}
