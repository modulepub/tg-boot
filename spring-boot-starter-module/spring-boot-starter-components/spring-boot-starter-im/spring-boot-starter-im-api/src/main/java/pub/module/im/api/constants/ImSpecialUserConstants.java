package pub.module.im.api.constants;

/**
 * IM 特殊用户常量
 */
public final class ImSpecialUserConstants {

    private ImSpecialUserConstants() {
    }

    /**
     * 管理端系统账号（与用户私信会话的发送方）
     */
    public static final String MGT_SYSTEM_USER_CODE = "system";

    /**
     * 系统通知用户编码
     */
    public static final String SYSTEM_NOTICE_USER_CODE = "TZ0001";

    /**
     * 系统通知用户名称
     */
    public static final String SYSTEM_NOTICE_USER_NAME = "系统通知";

    /**
     * 系统通知用户头像（喇叭形象）
     */
    public static final String SYSTEM_NOTICE_AVATAR =
            "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/svg/1f4e2.svg";
}
