package pub.module.im.crud.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.im.crud.entity.ImMessage;

import java.util.List;

public interface ImMessageService extends IService<ImMessage> {

    ImMessage getByCode(String imMessageCode);

    IPage<ImMessage> pageByConversation(String conversationCode, long pageNo, long pageSize);

    IPage<ImMessage> pageByConversation(String conversationCode, long pageNo, long pageSize, boolean asc);

    IPage<ImMessage> pageByParticipant(String userCode, long pageNo, long pageSize, boolean asc);

    List<ImMessage> listUnreadByToUser(String toUserCode);

    List<ImMessage> listUnreadByFromAndTo(String fromUserCode, String toUserCode);

    int countUnreadByToUser(String toUserCode);

    void markRead(List<String> messageCodes, String toUserCode);

    void logicDeleteByConversationCode(String conversationCode);
}
