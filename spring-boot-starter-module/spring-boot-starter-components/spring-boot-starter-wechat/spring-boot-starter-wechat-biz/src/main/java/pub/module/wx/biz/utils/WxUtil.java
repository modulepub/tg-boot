package pub.module.wx.biz.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.extra.spring.SpringUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        WxMaService wxMaService = SpringUtil.getBean(WxMaService.class);
        if(wxMaService.getWxMaConfig() == null){
                List<WxMaDefaultConfigImpl> wxMpConfigurations = new ArrayList<>();
                WxMaDefaultConfigImpl wxMaDefaultConfig = new WxMaDefaultConfigImpl();
                wxMaDefaultConfig.setAppid("wxd414b5861f8f2a9a");
                wxMaDefaultConfig.setSecret("e63b1412f02c95e0076cf737f83949b1");
                wxMpConfigurations.add(wxMaDefaultConfig);
                wxMaService.setMultiConfigs(
                        wxMpConfigurations.stream()
                                .map(a -> {
                                    WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
                                    config.setAppid(a.getAppid());
                                    config.setSecret(a.getSecret());
                                    //config.setToken(a.getWcToken());
                                    //config.setAesKey(a.getWcAesKey());
                                    config.setMsgDataFormat(a.getMsgDataFormat());
                                    return config;
                                }).collect(Collectors.toMap(WxMaDefaultConfigImpl::getAppid, a -> a, (o, n) -> o)));
            }
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


}
