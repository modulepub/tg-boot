package pub.module.wx.biz.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.wx.api.service.ApiWxMaSessionService;
import pub.module.wx.biz.config.WxMaRuntimeRefresher;

@Service
public class ApiWxMaSessionServiceImpl implements ApiWxMaSessionService {

    @Resource
    private WxMaService wxMaService;
    @Resource
    private WxMaRuntimeRefresher wxMaRuntimeRefresher;

    @Override
    public String getOpenIdByCode(String appId, String code) {
        return getSessionByCode(appId, code).getOpenId();
    }

    @Override
    public MaSessionDTO getSessionByCode(String appId, String code) {
        Assert.notBlank(appId, "appId 不能为空");
        Assert.notBlank(code, "code 不能为空");
        wxMaRuntimeRefresher.ensureLoaded();
        wxMaService.switchoverTo(appId.trim());
        try {
            WxMaUserService wxMaUserService = wxMaService.getUserService();
            WxMaJscode2SessionResult session = wxMaUserService.getSessionInfo(code.trim());
            Assert.notNull(session, "获取微信用户 session 失败");
            Assert.notBlank(session.getSessionKey(), "获取 sessionKey 失败");
            MaSessionDTO dto = new MaSessionDTO();
            dto.setOpenId(StrUtil.trim(session.getOpenid()));
            dto.setSessionKey(session.getSessionKey().trim());
            Assert.notBlank(dto.getOpenId(), "获取微信 openId 失败");
            return dto;
        }
        catch (Exception e) {
            throw new IllegalStateException("获取微信 session 失败：" + e.getMessage(), e);
        }
    }
}
