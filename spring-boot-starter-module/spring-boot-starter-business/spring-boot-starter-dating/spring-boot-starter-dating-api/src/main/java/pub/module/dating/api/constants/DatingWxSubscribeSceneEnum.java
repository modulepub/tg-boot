package pub.module.dating.api.constants;

import lombok.Getter;

/**
 * 婚恋域微信小程序订阅消息场景（模板 ID 由后台 wx_ma_subscribe_template 配置，wechat 组件负责解析与发送）。
 */
@Getter
public enum DatingWxSubscribeSceneEnum {

    /** 收到好友申请通知 — 面向被申请者 */
    FRIEND_REQUEST_RECEIVED(
            DatingWxSubscribeTemplateCode.FRIEND_REQUEST_RECEIVED,
            "收到好友申请通知",
            "pages-sub/dating/DtCustomer/friend-list"
    ),

    /** 添加好友成功通知 — 面向申请者 */
    FRIEND_ADD_SUCCESS(
            DatingWxSubscribeTemplateCode.FRIEND_ADD_SUCCESS,
            "添加好友成功通知",
            "pages-sub/dating/DtCustomer/friend-list"
    ),

    /** 牵线请求通知 — 面向红娘 */
    MATCH_REQUEST(
            DatingWxSubscribeTemplateCode.MATCH_REQUEST,
            "牵线请求通知",
            "pages-sub/dating/matchmaker/line-list"
    ),

    /** 相亲对象推荐通知 — 免费推荐成功后提醒用户查看 */
    FREE_RECOMMEND(
            DatingWxSubscribeTemplateCode.FREE_RECOMMEND,
            "相亲对象推荐通知",
            "pages/index/index"
    ),
    ;

    private final String templateCode;
    private final String desc;
    /** 小程序 page，不含开头 / */
    private final String jumpPage;

    DatingWxSubscribeSceneEnum(String templateCode, String desc, String jumpPage) {
        this.templateCode = templateCode;
        this.desc = desc;
        this.jumpPage = jumpPage;
    }
}
