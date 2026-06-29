package pub.module.ai.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.ai.crud.entity.AiAgent;
import pub.module.ai.crud.mapper.AiAgentMapper;
import pub.module.ai.crud.service.IAiAgentService;

import java.util.Collection;

@Service
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgent> implements IAiAgentService {

    private static final String BIZ_CODE = "aiAgentCode";

    @Override
    public AiAgent getByCode(String aiAgentCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<AiAgent>().eq(StrUtil.toUnderlineCase(BIZ_CODE), aiAgentCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AiAgent entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE, IdUtil.getSnowflakeNextIdStr());
        } else {
            Assert.isNull(getByCode(code.toString()), "智能体编码已存在");
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AiAgent entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "aiAgentCode 不能为空");
        AiAgent existing = getByCode(code.toString());
        Assert.notNull(existing, "智能体不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "智能体编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> aiAgentCodes) {
        Assert.notEmpty(aiAgentCodes, "请选择要删除的智能体");
        for (String code : aiAgentCodes) {
            AiAgent row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
