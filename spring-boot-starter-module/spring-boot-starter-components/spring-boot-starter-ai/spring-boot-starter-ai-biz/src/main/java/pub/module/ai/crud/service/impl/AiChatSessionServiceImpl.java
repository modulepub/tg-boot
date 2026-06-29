package pub.module.ai.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.ai.crud.entity.AiChatSession;
import pub.module.ai.crud.mapper.AiChatSessionMapper;
import pub.module.ai.crud.service.IAiChatSessionService;

import java.util.Collection;

@Service
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSession> implements IAiChatSessionService {

    private static final String BIZ_CODE = "aiChatSessionCode";

    @Override
    public AiChatSession getByCode(String aiChatSessionCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<AiChatSession>().eq(StrUtil.toUnderlineCase(BIZ_CODE), aiChatSessionCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AiChatSession entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE, IdUtil.getSnowflakeNextIdStr());
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> aiChatSessionCodes) {
        Assert.notEmpty(aiChatSessionCodes, "请选择要删除的会话");
        for (String code : aiChatSessionCodes) {
            AiChatSession row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
