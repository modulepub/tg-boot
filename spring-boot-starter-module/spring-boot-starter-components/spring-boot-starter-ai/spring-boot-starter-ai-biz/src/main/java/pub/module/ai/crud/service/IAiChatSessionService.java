package pub.module.ai.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.ai.crud.entity.AiChatSession;

import java.util.Collection;

public interface IAiChatSessionService extends IService<AiChatSession> {

    AiChatSession getByCode(String aiChatSessionCode);

    boolean removeByBizCodes(Collection<String> aiChatSessionCodes);
}
