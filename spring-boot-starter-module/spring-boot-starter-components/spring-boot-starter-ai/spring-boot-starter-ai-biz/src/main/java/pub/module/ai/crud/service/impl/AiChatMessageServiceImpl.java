package pub.module.ai.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.ai.crud.entity.AiChatMessage;
import pub.module.ai.crud.mapper.AiChatMessageMapper;
import pub.module.ai.crud.service.IAiChatMessageService;

import java.util.List;

@Service
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessage> implements IAiChatMessageService {

    private static final String BIZ_CODE = "aiChatMessageCode";

    @Override
    public AiChatMessage getByCode(String aiChatMessageCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<AiChatMessage>().eq(StrUtil.toUnderlineCase(BIZ_CODE), aiChatMessageCode), false);
    }

    @Override
    public List<AiChatMessage> listBySessionCode(String aiChatSessionCode) {
        return list(new QueryWrapper<AiChatMessage>()
                .eq(StrUtil.toUnderlineCase("aiChatSessionCode"), aiChatSessionCode)
                .orderByAsc(StrUtil.toUnderlineCase("aiChatMessageSortNo")));
    }

    @Override
    public int nextSortNo(String aiChatSessionCode) {
        AiChatMessage last = getOne(new QueryWrapper<AiChatMessage>()
                .eq(StrUtil.toUnderlineCase("aiChatSessionCode"), aiChatSessionCode)
                .orderByDesc(StrUtil.toUnderlineCase("aiChatMessageSortNo"))
                .last("LIMIT 1"), false);
        return last == null || last.getAiChatMessageSortNo() == null ? 1 : last.getAiChatMessageSortNo() + 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AiChatMessage entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE, IdUtil.getSnowflakeNextIdStr());
        }
        if (entity.getAiChatMessageSortNo() == null && StrUtil.isNotBlank(entity.getAiChatSessionCode())) {
            entity.setAiChatMessageSortNo(nextSortNo(entity.getAiChatSessionCode()));
        }
        return getBaseMapper().insert(entity) > 0;
    }
}
