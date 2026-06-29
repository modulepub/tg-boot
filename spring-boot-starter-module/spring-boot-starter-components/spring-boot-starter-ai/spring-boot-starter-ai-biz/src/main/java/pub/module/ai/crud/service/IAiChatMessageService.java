package pub.module.ai.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.ai.crud.entity.AiChatMessage;

import java.util.List;

public interface IAiChatMessageService extends IService<AiChatMessage> {

    AiChatMessage getByCode(String aiChatMessageCode);

    List<AiChatMessage> listBySessionCode(String aiChatSessionCode);

    int nextSortNo(String aiChatSessionCode);
}
