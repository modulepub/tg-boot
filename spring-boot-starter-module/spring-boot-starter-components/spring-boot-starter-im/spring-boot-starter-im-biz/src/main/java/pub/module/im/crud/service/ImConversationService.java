package pub.module.im.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.im.crud.entity.ImConversation;

import java.util.List;

public interface ImConversationService extends IService<ImConversation> {

    ImConversation getByUserPair(String userACode, String userBCode);

    List<ImConversation> listByUserCode(String userCode);

    void updateLastMessage(String conversationCode, String lastMessageCode);

    void incrementUnread(String conversationCode, String forUserCode);

    void decrementUnread(String conversationCode, String forUserCode, int count);

    void clearByUserPair(String userCodeA, String userCodeB);
}
