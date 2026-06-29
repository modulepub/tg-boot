package pub.module.wx.api.service;

import lombok.Data;

/**
 * 微信小程序会话能力（code 换 openId 等），与业务域解耦。
 */
public interface ApiWxMaSessionService {

    @Data
    class MaSessionDTO {
        private String openId;
        private String sessionKey;
    }

    /**
     * 使用小程序登录 code 换取 openId。
     */
    String getOpenIdByCode(String appId, String code);

    /**
     * 使用小程序登录 code 换取 openId 与 sessionKey（虚拟支付签名需要）。
     */
    MaSessionDTO getSessionByCode(String appId, String code);
}
