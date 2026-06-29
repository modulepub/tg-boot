package pub.module.ai.crud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pub.module.ai.crud.entity.AiChatMessage;

@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {
}
