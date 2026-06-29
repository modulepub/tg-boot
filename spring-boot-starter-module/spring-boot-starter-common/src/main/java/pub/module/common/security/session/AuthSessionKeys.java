package pub.module.common.security.session;

import cn.hutool.core.util.StrUtil;

public final class AuthSessionKeys {

    private AuthSessionKeys() {
    }

    public static String loginSessionKey(String userCode, String jti) {
        String normalizedUserCode = StrUtil.trim(userCode);
        String normalizedJti = StrUtil.trim(jti);
        if (StrUtil.isBlank(normalizedJti)) {
            return normalizedUserCode;
        }
        return normalizedUserCode + ':' + normalizedJti;
    }
}
