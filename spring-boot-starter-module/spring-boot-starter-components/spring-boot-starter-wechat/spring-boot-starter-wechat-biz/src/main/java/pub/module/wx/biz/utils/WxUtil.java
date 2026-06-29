package pub.module.wx.biz.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.extra.spring.SpringUtil;
import pub.module.wx.biz.config.WxMaRuntimeRefresher;
import pub.module.wx.biz.config.WxMpRuntimeRefresher;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import pub.module.wx.biz.vo.LoginRequest;
import pub.module.wx.biz.vo.WxMaUserInfoEx;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Set;

/**
 * 微信工具类
 * 提供微信小程序和公众号相关的工具方法
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
public class WxUtil {

    @SneakyThrows
    public static WxMaUserInfoEx getWxMaUserInfo(LoginRequest request) {
        init();
        Assert.hasText(request.getCode(), "code不能为空");
        Assert.hasText(request.getAppId(), "appId不能为空");
        WxMaService wxMaService = SpringUtil.getBean(WxMaService.class);
        wxMaService.switchoverTo(request.getAppId());
        // 获取微信用户session
        WxMaUserService wxMaUserService = wxMaService.getUserService();
        WxMaJscode2SessionResult session =wxMaUserService.getSessionInfo(request.getCode());
        Assert.notNull(session, "获取微信用户session失败，请检查微信公众平台相关参数");
        WxMaUserInfoEx result = new WxMaUserInfoEx();
        result.setOpenId(session.getOpenid());
        return result;
    }

    @SneakyThrows
    public static WxMpUser getWxMpUserInfo(LoginRequest request) {
        init();
        Assert.hasText(request.getCode(), "code不能为空");
        Assert.hasText(request.getAppId(), "appId不能为空");
        WxMpService wxMpService = SpringUtil.getBean(WxMpService.class);
        wxMpService.switchoverTo(request.getAppId());
        WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.getOAuth2Service().getAccessToken(request.getCode());
        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxOAuth2AccessToken.getOpenId());
        log.info("微信公众号用户信息--{}", wxMpUser.toString());
        return wxMpUser;
    }

    public static void init() {
        SpringUtil.getBean(WxMaRuntimeRefresher.class).ensureLoaded();
        SpringUtil.getBean(WxMpRuntimeRefresher.class).ensureLoaded();
    }



    /**
     * 订阅消息发送
     */
    @SneakyThrows(Exception.class)
    public static void sendSubMessage(WxMaSubscribeMessage sendSubMessage) {
        WxMaService wxMaService = SpringUtil.getBean(WxMaService.class);
        wxMaService.getSubscribeService().sendSubscribeMsg(sendSubMessage);
    }

    /**
     * 发送微信公众号客服消息
     */
    @SneakyThrows(WxErrorException.class)
    public static void sendMpKfMessage(WxMpKefuMessage mpMessage) {
        WxMpService wxMpService = SpringUtil.getBean(WxMpService.class);
        wxMpService.getKefuService().sendKefuMessage(mpMessage);
    }

    /**
     * 发送微信公众号消息
     */
    @SneakyThrows(WxErrorException.class)
    public static void sendMpTmMessage(WxMpTemplateMessage mpMessage) {
        log.info("发送公众号消息--{}",mpMessage.toJson());
        WxMpService wxMpService = SpringUtil.getBean(WxMpService.class);
        wxMpService.getTemplateMsgService().sendTemplateMsg(mpMessage);
    }



    /**
     * String type; 素材类型
     * Integer offset; ……
     * Integer count; ……
     */
    @SneakyThrows
    public static String getMaterial(JSONObject material) {
        WxMpService wxMpService = SpringUtil.getBean(WxMpService.class);
        return wxMpService.post(" https://api.weixin.qq.com/cgi-bin/material/batchget_material", material);
    }

    private static final Set<String> POSTER_IMAGE_ALLOWED_HOSTS = Set.of(
            "pubpicture.oss-cn-shenzhen.aliyuncs.com",
            "matchlove.oss-cn-beijing.aliyuncs.com"
    );

    /**
     * 服务端拉取 OSS 图片并转为 data URL，供 H5 海报 canvas 使用（避免浏览器跨域）。
     */
    public static String fetchPosterImageDataUrl(String imageUrl) {
        String url = String.valueOf(imageUrl == null ? "" : imageUrl).trim();
        Assert.hasText(url, "图片地址不能为空");
        URI uri;
        try {
            uri = URI.create(url);
        } catch (Exception e) {
            throw new IllegalArgumentException("图片地址不合法");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("仅支持 https 图片");
        }
        String host = String.valueOf(uri.getHost()).toLowerCase();
        boolean allowed = POSTER_IMAGE_ALLOWED_HOSTS.stream()
                .anyMatch(h -> h.equalsIgnoreCase(host));
        if (!allowed) {
            throw new IllegalArgumentException("图片域名不在白名单");
        }
        byte[] bytes = HttpUtil.downloadBytes(url);
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("图片内容为空");
        }
        String lower = url.toLowerCase();
        String mime = lower.contains(".png") ? "image/png" : "image/jpeg";
        return "data:" + mime + ";base64," + cn.hutool.core.codec.Base64.encode(bytes);
    }

    /**
     * 生成小程序码（无数量限制接口），scene 最长 32 字符。
     */
    public static byte[] getWxaCodeUnlimit(String appId, String page, String scene, String envVersion)
            throws WxErrorException {
        return getWxaCodeUnlimit(appId, page, scene, envVersion, null);
    }

    /**
     * 生成小程序码（无数量限制接口），scene 最长 32 字符。
     *
     * @param width 可选，微信允许 280–1280；null 时默认 430（海报内嵌）
     */
    public static byte[] getWxaCodeUnlimit(String appId, String page, String scene, String envVersion, Integer width)
            throws WxErrorException {
        init();
        Assert.hasText(appId, "appId不能为空");
        Assert.hasText(page, "page不能为空");
        Assert.hasText(scene, "scene不能为空");
        if (scene.length() > 32) {
            throw new IllegalArgumentException("scene最长32字符");
        }
        String pagePath = page.startsWith("/") ? page.substring(1) : page;
        String env = String.valueOf(envVersion == null ? "" : envVersion).trim();
        if (!"develop".equals(env) && !"trial".equals(env) && !"release".equals(env)) {
            env = "develop";
        }
        // develop/trial 未正式发布时 checkPath=true 会 41030 invalid page；仅正式版校验路径
        boolean checkPath = "release".equals(env);
        WxMaService wxMaService = SpringUtil.getBean(WxMaService.class);
        wxMaService.switchoverTo(appId);
        int qrWidth = width == null ? 430 : width;
        if (qrWidth < 280) {
            qrWidth = 280;
        } else if (qrWidth > 1280) {
            qrWidth = 1280;
        }
        return wxMaService.getQrcodeService().createWxaCodeUnlimitBytes(
                scene,
                pagePath,
                checkPath,
                env,
                qrWidth,
                true,
                null,
                false
        );
    }


}
