package pub.module.wx.biz.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;
import pub.module.wx.api.dto.WxMaMediaCheckAsyncRequest;
import pub.module.wx.api.dto.WxMaMediaCheckAsyncResult;
import pub.module.wx.api.dto.WxMaMsgSecCheckRequest;
import pub.module.wx.api.dto.WxMaMsgSecCheckResult;
import pub.module.wx.api.service.ApiWxMaContentSecurityService;
import pub.module.wx.biz.config.WxMaRuntimeRefresher;
import pub.module.wx.crud.entity.WxMiniConfig;
import pub.module.wx.crud.service.WxMiniConfigService;

@Slf4j
@Service
public class ApiWxMaContentSecurityServiceImpl implements ApiWxMaContentSecurityService {

    private static final String MSG_SEC_CHECK_URL = "https://api.weixin.qq.com/wxa/msg_sec_check?access_token=%s";
    private static final String MEDIA_CHECK_ASYNC_URL = "https://api.weixin.qq.com/wxa/media_check_async?access_token=%s";
    private static final int API_VERSION = 2;

    @Resource
    private WxMaService wxMaService;
    @Resource
    private WxMaRuntimeRefresher wxMaRuntimeRefresher;
    @Resource
    private WxMiniConfigService wxMiniConfigService;

    @Override
    public WxMaMsgSecCheckResult msgSecCheck(WxMaMsgSecCheckRequest request) {
        try {
            String appId = resolveAppId(request.getWxMaAppId());
            switchApp(appId);
            JSONObject body = new JSONObject();
            body.set("openid", request.getOpenId());
            body.set("scene", request.getScene());
            body.set("version", API_VERSION);
            body.set("content", request.getContent());
            JSONObject resp = postJson(String.format(MSG_SEC_CHECK_URL, wxMaService.getAccessToken()), body);
            return parseMsgSecCheck(resp);
        } catch (Exception ex) {
            log.warn("微信 msgSecCheck 调用异常", ex);
            return WxMaMsgSecCheckResult.builder()
                    .apiReachable(false)
                    .errMsg(ex.getMessage())
                    .build();
        }
    }

    @Override
    public WxMaMediaCheckAsyncResult mediaCheckAsync(WxMaMediaCheckAsyncRequest request) {
        try {
            String appId = resolveAppId(request.getWxMaAppId());
            switchApp(appId);
            JSONObject body = new JSONObject();
            body.set("openid", request.getOpenId());
            body.set("scene", request.getScene());
            body.set("version", API_VERSION);
            body.set("media_url", request.getMediaUrl());
            body.set("media_type", request.getMediaType());
            JSONObject resp = postJson(String.format(MEDIA_CHECK_ASYNC_URL, wxMaService.getAccessToken()), body);
            return parseMediaCheckAsync(resp);
        } catch (Exception ex) {
            log.warn("微信 mediaCheckAsync 调用异常", ex);
            return WxMaMediaCheckAsyncResult.builder()
                    .apiReachable(false)
                    .errMsg(ex.getMessage())
                    .build();
        }
    }

    @Override
    public String resolveDefaultAppId() {
        return resolveAppId(null);
    }

    private String resolveAppId(String preferredAppId) {
        if (StrUtil.isNotBlank(preferredAppId)) {
            return preferredAppId.trim();
        }
        WxMiniConfig row = wxMiniConfigService.lambdaQuery()
                .eq(WxMiniConfig::getWxMiniConfigEnabledCode, "1")
                .orderByAsc(WxMiniConfig::getSeqNo)
                .orderByAsc(WxMiniConfig::getWxMiniConfigCode)
                .last("LIMIT 1")
                .one();
        if (row == null || StrUtil.isBlank(row.getWxMiniConfigAppId())) {
            throw new IllegalStateException("未配置启用的微信小程序（wx_mini_config）");
        }
        return row.getWxMiniConfigAppId().trim();
    }

    private void switchApp(String appId) throws WxErrorException {
        wxMaRuntimeRefresher.ensureLoaded();
        wxMaService.switchoverTo(appId);
    }

    private static JSONObject postJson(String url, JSONObject body) {
        try (HttpResponse response = HttpRequest.post(url)
                .body(body.toString())
                .timeout(15000)
                .execute()) {
            return JSONUtil.parseObj(response.body());
        }
    }

    private static WxMaMsgSecCheckResult parseMsgSecCheck(JSONObject resp) {
        int errCode = resp.getInt("errcode", -1);
        if (errCode != 0) {
            return WxMaMsgSecCheckResult.builder()
                    .apiReachable(false)
                    .errCode(errCode)
                    .errMsg(resp.getStr("errmsg"))
                    .rawSummary(resp.toString())
                    .build();
        }
        JSONObject result = resp.getJSONObject("result");
        String suggest = result != null ? result.getStr("suggest") : null;
        Integer label = result != null ? result.getInt("label") : null;
        return WxMaMsgSecCheckResult.builder()
                .apiReachable(true)
                .errCode(0)
                .errMsg(resp.getStr("errmsg"))
                .suggest(suggest)
                .label(label)
                .traceId(resp.getStr("trace_id"))
                .rawSummary(resp.toString())
                .build();
    }

    private static WxMaMediaCheckAsyncResult parseMediaCheckAsync(JSONObject resp) {
        int errCode = resp.getInt("errcode", -1);
        if (errCode != 0) {
            return WxMaMediaCheckAsyncResult.builder()
                    .apiReachable(false)
                    .errCode(errCode)
                    .errMsg(resp.getStr("errmsg"))
                    .rawSummary(resp.toString())
                    .build();
        }
        return WxMaMediaCheckAsyncResult.builder()
                .apiReachable(true)
                .errCode(0)
                .errMsg(resp.getStr("errmsg"))
                .traceId(resp.getStr("trace_id"))
                .rawSummary(resp.toString())
                .build();
    }
}
