package pub.module.ai.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.ai.crud.entity.AiApiConfig;
import pub.module.ai.crud.mapper.AiApiConfigMapper;
import pub.module.ai.crud.service.IAiApiConfigService;

import java.util.Collection;

@Service
public class AiApiConfigServiceImpl extends ServiceImpl<AiApiConfigMapper, AiApiConfig> implements IAiApiConfigService {

    private static final String BIZ_CODE = "aiApiConfigCode";

    @Override
    public AiApiConfig getByCode(String aiApiConfigCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<AiApiConfig>().eq(StrUtil.toUnderlineCase(BIZ_CODE), aiApiConfigCode), false);
    }

    @Override
    public AiApiConfig getFirstEnabled() {
        return getOne(new QueryWrapper<AiApiConfig>()
                .eq(StrUtil.toUnderlineCase("aiApiConfigEnabledCode"), "1")
                .orderByAsc("create_time")
                .last("LIMIT 1"), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AiApiConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE, IdUtil.getSnowflakeNextIdStr());
        } else {
            Assert.isNull(getByCode(code.toString()), "AI 接口配置编码已存在");
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AiApiConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "aiApiConfigCode 不能为空");
        AiApiConfig existing = getByCode(code.toString());
        Assert.notNull(existing, "AI 接口配置不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "配置编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> aiApiConfigCodes) {
        Assert.notEmpty(aiApiConfigCodes, "请选择要删除的配置");
        for (String code : aiApiConfigCodes) {
            AiApiConfig row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
