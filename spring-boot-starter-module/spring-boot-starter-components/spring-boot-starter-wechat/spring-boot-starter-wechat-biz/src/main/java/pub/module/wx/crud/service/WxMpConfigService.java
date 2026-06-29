package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxMpConfig;

import java.util.Collection;

/**
 * 微信公众号配置 CRUD 服务。
 */
public interface WxMpConfigService extends IService<WxMpConfig> {

    WxMpConfig getByCode(String wxMpConfigCode);

    WxMpConfig getByAppId(String appId);

    /**
     * 按 AppId 查找配置（回调路由用，不限制启用状态）。
     */
    WxMpConfig findByAppId(String appId);

    boolean removeByBizCodes(Collection<String> wxMpConfigCodes);
}
