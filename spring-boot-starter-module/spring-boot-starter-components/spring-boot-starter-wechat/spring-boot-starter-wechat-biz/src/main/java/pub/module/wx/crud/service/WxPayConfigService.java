package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxPayConfig;

import java.util.Collection;

/**
 * 微信支付配置 CRUD 服务。
 */
public interface WxPayConfigService extends IService<WxPayConfig> {

    WxPayConfig getByCode(String wxPayConfigCode);

    boolean removeByBizCodes(Collection<String> wxPayConfigCodes);
}
