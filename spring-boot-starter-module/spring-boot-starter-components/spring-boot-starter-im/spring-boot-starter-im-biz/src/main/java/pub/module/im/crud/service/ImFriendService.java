package pub.module.im.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.im.crud.entity.ImFriend;

import java.util.List;

public interface ImFriendService extends IService<ImFriend> {

    ImFriend getByUserAndFriend(String userCode, String friendUserCode);

    List<ImFriend> listByUserCode(String userCode);

    void removeFriendBidirectional(String userCodeA, String userCodeB);
}
