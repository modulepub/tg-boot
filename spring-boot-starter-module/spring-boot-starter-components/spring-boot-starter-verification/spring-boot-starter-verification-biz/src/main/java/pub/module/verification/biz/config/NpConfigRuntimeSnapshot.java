package pub.module.verification.biz.config;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 从 vt_np_config 加载的二要素运行时快照（不可变，线程安全读）。
 */
@Getter
public final class NpConfigRuntimeSnapshot {

    /** 实人认证 Cloudauth · Mobile2MetaVerify（云盾-信息核验-手机号二要素） */
    public static final String PROVIDER_ALIYUN_CLOUDAUTH = "aliyun_cloudauth";

    /** @deprecated 旧版号码百科 dytns，请改用 {@link #PROVIDER_ALIYUN_CLOUDAUTH} */
    @Deprecated
    public static final String PROVIDER_ALIYUN = "aliyun_dytns";

    private final boolean enabled;
    private final String providerCode;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String endpoint;
    private final String mask;
    private final String npConfigCode;

    private NpConfigRuntimeSnapshot(boolean enabled, String providerCode, String accessKeyId,
            String accessKeySecret, String endpoint, String mask, String npConfigCode) {
        this.enabled = enabled;
        this.providerCode = providerCode;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.endpoint = endpoint;
        this.mask = mask;
        this.npConfigCode = npConfigCode;
    }

    public static NpConfigRuntimeSnapshot disabled() {
        return new NpConfigRuntimeSnapshot(false, PROVIDER_ALIYUN_CLOUDAUTH, null, null,
                "cloudauth.aliyuncs.com", "normal", null);
    }

    public static NpConfigRuntimeSnapshot fromRow(String npConfigCode, String providerCode,
            String accessKeyId, String accessKeySecret, String authCodeIgnored, String endpoint, String mask) {
        String normalizedProvider = StrUtil.blankToDefault(providerCode, PROVIDER_ALIYUN_CLOUDAUTH);
        if (PROVIDER_ALIYUN.equalsIgnoreCase(normalizedProvider)) {
            normalizedProvider = PROVIDER_ALIYUN_CLOUDAUTH;
        }
        return new NpConfigRuntimeSnapshot(true,
                normalizedProvider,
                StrUtil.trim(accessKeyId),
                StrUtil.trim(accessKeySecret),
                StrUtil.blankToDefault(endpoint, "cloudauth.aliyuncs.com"),
                StrUtil.blankToDefault(mask, "normal"),
                npConfigCode);
    }

    public boolean isAliyunReady() {
        return enabled
                && PROVIDER_ALIYUN_CLOUDAUTH.equalsIgnoreCase(providerCode)
                && !StrUtil.hasBlank(accessKeyId, accessKeySecret);
    }
}
