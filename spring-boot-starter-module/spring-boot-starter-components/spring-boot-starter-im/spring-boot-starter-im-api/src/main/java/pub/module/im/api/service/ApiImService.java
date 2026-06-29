package pub.module.im.api.service;

import pub.module.im.api.service.dto.ImAddFriendDTO;
import pub.module.im.api.service.dto.ImAccountDTO;

/**
 * 用户端-IM账号服务
 */
public interface ApiImService {

    /**
     * 用户端-创建或更新IM账号
     *
     * @param accountDTO 账号参数
     */
    void saveOrUpdateAccount(ImAccountDTO accountDTO);

    /**
     * 用户端-添加好友
     *
     * @param addFriendDTO 添加好友参数
     */
    void addFriend(ImAddFriendDTO addFriendDTO);

    /**
     * 双向删除 IM 好友关系
     *
     * @param userCodeA 用户 A（IM Identifier）
     * @param userCodeB 用户 B（IM Identifier）
     */
    void removeFriendBidirectional(String userCodeA, String userCodeB);

    /**
     * 双向删除 C2C 会话并清理漫游消息
     *
     * @param userCodeA 用户 A（IM Identifier）
     * @param userCodeB 用户 B（IM Identifier）
     */
    void clearC2cChatBidirectional(String userCodeA, String userCodeB);

    /**
     * 通知对方：联系人关系已解除（自定义 C2C 消息，用于对方客户端清理会话）
     *
     * @param fromUserCode 发起解除方
     * @param toUserCode   被通知方
     */
    void notifyContactRemoved(String fromUserCode, String toUserCode);

    /**
     * 生成 IM 连接凭证（兼容旧接口；本地实现返回占位符）
     *
     * @param userCode IM Identifier
     * @return 本地连接标识
     */
    String generateUserSig(String userCode);

    /**
     * 发送 C2C 文本消息
     *
     * @param fromUserCode 发送方用户编码
     * @param toUserCode   接收方用户编码
     * @param text         文本内容
     */
    void sendC2CTextMessage(String fromUserCode, String toUserCode, String text);

    /**
     * 发送 C2C 图文消息（自定义消息）
     *
     * @param fromUserCode 发送方用户编码
     * @param toUserCode   接收方用户编码
     * @param title        图文标题
     * @param imageUrl     图片地址
     * @param linkUrl      跳转链接
     */
    void sendC2CRichMessage(String fromUserCode, String toUserCode, String title, String text, String imageUrl, String linkUrl);

    /**
     * 退出登录（本地 IM 无 UserSig 缓存，保留接口兼容）
     */
    void logoutByUserCode(String userCode);
}
