package pub.module.verification.api.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 内容合法校验插件编码
 */
@Getter
@RequiredArgsConstructor
public enum ContentModerationPluginCodeEnum {

    /** 微信小程序 msgSecCheck + mediaCheckAsync */
    WECHAT_MEDIA_CHECK("wechat_media_check");

    private final String code;
}
