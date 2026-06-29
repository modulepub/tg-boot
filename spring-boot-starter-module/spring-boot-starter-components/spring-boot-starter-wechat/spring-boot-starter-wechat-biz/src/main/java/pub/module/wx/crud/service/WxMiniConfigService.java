package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxMiniConfig;

import java.util.Collection;

/**
 * 微信小程序配置 CRUD 服务。
 */
public interface WxMiniConfigService extends IService<WxMiniConfig> {

    WxMiniConfig getByCode(String wxMiniConfigCode);

    boolean removeByBizCodes(Collection<String> wxMiniConfigCodes);
}
