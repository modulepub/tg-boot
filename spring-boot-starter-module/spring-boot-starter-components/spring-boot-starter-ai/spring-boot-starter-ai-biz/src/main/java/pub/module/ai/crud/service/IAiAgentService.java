package pub.module.ai.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.ai.crud.entity.AiAgent;

import java.util.Collection;

public interface IAiAgentService extends IService<AiAgent> {

    AiAgent getByCode(String aiAgentCode);

    boolean removeByBizCodes(Collection<String> aiAgentCodes);
}
