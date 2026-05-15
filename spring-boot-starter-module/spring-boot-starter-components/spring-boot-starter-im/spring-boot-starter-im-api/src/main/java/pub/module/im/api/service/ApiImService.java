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
     * 生成指定 IM 用户的 UserSig
     *
     * @param userCode IM Identifier
     * @return UserSig
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
    void sendC2CRichMessage(String fromUserCode, String toUserCode, String title, String imageUrl, String linkUrl);
}
