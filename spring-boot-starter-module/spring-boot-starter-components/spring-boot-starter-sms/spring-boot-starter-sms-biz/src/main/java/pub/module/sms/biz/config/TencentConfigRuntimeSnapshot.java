package pub.module.sms.biz.config;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 从 sms_tencent_config 加载的腾讯云短信运行时快照（不可变，线程安全读）。
 */
@Getter
public final class TencentConfigRuntimeSnapshot {

    public static final String DEFAULT_REGION = "ap-guangzhou";

    private final boolean enabled;
    private final String smsTencentConfigCode;
    private final String secretId;
    private final String secretKey;
    private final String sdkAppId;
    private final String signName;
    private final String region;

    private TencentConfigRuntimeSnapshot(boolean enabled, String smsTencentConfigCode, String secretId,
            String secretKey, String sdkAppId, String signName, String region) {
        this.enabled = enabled;
        this.smsTencentConfigCode = smsTencentConfigCode;
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.sdkAppId = sdkAppId;
        this.signName = signName;
        this.region = region;
    }

    public static TencentConfigRuntimeSnapshot disabled() {
        return new TencentConfigRuntimeSnapshot(false, null, null, null, null, null, DEFAULT_REGION);
    }

    public static TencentConfigRuntimeSnapshot fromRow(String smsTencentConfigCode, String secretId,
            String secretKey, String sdkAppId, String signName, String region) {
        return new TencentConfigRuntimeSnapshot(true,
                smsTencentConfigCode,
                StrUtil.trim(secretId),
                StrUtil.trim(secretKey),
                StrUtil.trim(sdkAppId),
                StrUtil.trim(signName),
                StrUtil.blankToDefault(region, DEFAULT_REGION));
    }

    public boolean isReady() {
        return enabled && !StrUtil.hasBlank(secretId, secretKey, sdkAppId, signName);
    }
}
