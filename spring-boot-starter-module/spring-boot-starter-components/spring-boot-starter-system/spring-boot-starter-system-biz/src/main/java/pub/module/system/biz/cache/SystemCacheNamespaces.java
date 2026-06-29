package pub.module.system.biz.cache;

/**
 * system 域短时缓存 namespace（经 {@link pub.module.common.cache.TgEphemeralCache}）。
 */
public final class SystemCacheNamespaces {

    public static final String SMS = "sys:sms";
    public static final String CAPTCHA = "sys:captcha";

    private SystemCacheNamespaces() {
    }
}
