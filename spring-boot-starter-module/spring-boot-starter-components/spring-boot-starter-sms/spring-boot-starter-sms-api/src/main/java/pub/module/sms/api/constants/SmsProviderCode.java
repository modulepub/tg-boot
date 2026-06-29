package pub.module.sms.api.constants;

import lombok.Getter;

/**
 * 短信渠道编码（跨模块发送时指定 plateCode / providerCode）。
 */
@Getter
public enum SmsProviderCode {

    TENCENT("tencent", "腾讯云短信"),
    CHUANG_LAN("chuangLan", "创蓝短信"),
    MOS("mosSmsSdk", "玄武 MOS 短信"),
    ;

    private final String code;
    private final String desc;

    SmsProviderCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
