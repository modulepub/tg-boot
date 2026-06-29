package pub.module.dating.api.constants;

/**
 * 婚恋域微信小程序订阅消息模板编码（与 wx_ma_subscribe_template 表及发送逻辑绑定，新增须执行 seed SQL）。
 */
public final class DatingWxSubscribeTemplateCode {

    /** 收到好友申请通知 — 面向被申请者 */
    public static final String FRIEND_REQUEST_RECEIVED = "friendRequestReceived";

    /** 添加好友成功通知 — 面向申请者 */
    public static final String FRIEND_ADD_SUCCESS = "friendAddSuccess";

    /** 牵线请求通知 — 面向红娘 */
    public static final String MATCH_REQUEST = "matchRequest";

    /** 相亲对象推荐通知 */
    public static final String FREE_RECOMMEND = "freeRecommend";

    private DatingWxSubscribeTemplateCode() {
    }
}
