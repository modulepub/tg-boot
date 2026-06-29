package pub.module.wx.biz.config;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 虚拟支付运行时配置快照（由 wx_virtual_pay_config 加载）。
 */
@Data
public class WxVirtualPayRuntimeConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String configCode;
    private String appId;
    private String offerId;
    private String appKeySandbox;
    private String appKeyProd;
    private boolean sandbox;
    private String notifyUrl;

    public String resolveAppKey() {
        return sandbox ? appKeySandbox : appKeyProd;
    }

    public int resolveEnv() {
        return sandbox ? 1 : 0;
    }
}
