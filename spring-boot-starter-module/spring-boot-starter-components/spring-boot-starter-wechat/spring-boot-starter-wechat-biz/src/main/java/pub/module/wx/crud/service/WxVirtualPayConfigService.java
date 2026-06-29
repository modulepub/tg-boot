package pub.module.wx.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.wx.crud.entity.WxVirtualPayConfig;

import java.util.Collection;

/**
 * 微信小程序虚拟支付配置 CRUD 服务。
 */
public interface WxVirtualPayConfigService extends IService<WxVirtualPayConfig> {

    WxVirtualPayConfig getByCode(String wxVirtualPayConfigCode);

    boolean removeByBizCodes(Collection<String> wxVirtualPayConfigCodes);
}
