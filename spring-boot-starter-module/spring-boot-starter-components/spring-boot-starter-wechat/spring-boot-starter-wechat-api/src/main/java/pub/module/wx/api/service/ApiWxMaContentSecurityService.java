package pub.module.wx.api.service;

import pub.module.wx.api.dto.WxMaMediaCheckAsyncRequest;
import pub.module.wx.api.dto.WxMaMediaCheckAsyncResult;
import pub.module.wx.api.dto.WxMaMsgSecCheckRequest;
import pub.module.wx.api.dto.WxMaMsgSecCheckResult;

/**
 * 微信小程序内容安全 API（msgSecCheck / mediaCheckAsync）
 */
public interface ApiWxMaContentSecurityService {

    WxMaMsgSecCheckResult msgSecCheck(WxMaMsgSecCheckRequest request);

    WxMaMediaCheckAsyncResult mediaCheckAsync(WxMaMediaCheckAsyncRequest request);

    /**
     * 返回当前启用的首个小程序 appId
     */
    String resolveDefaultAppId();
}
