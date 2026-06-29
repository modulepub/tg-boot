package pub.module.im.api.service;

import pub.module.im.api.service.dto.ImAddFriendDTO;

import java.util.List;

/**
 * IM 好友管理服务
 */
public interface ApiImFriendService {

    /**
     * 添加好友（双向，用于业务端同意申请后建立 IM 好友关系）
     */
    void addFriend(ImAddFriendDTO addFriendDTO);

    /**
     * 是否已为好友（双向关系已建立且状态正常）
     */
    boolean isFriend(String userCodeA, String userCodeB);

    /**
     * 双向删除好友
     */
    void removeFriendBidirectional(String userCodeA, String userCodeB);

    /**
     * 查询好友列表
     */
    List<?> listFriends(String userCode);
}
