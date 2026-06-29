package pub.module.verification.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.verification.crud.entity.NpConfig;
import pub.module.verification.crud.mapper.NpConfigMapper;
import pub.module.verification.crud.service.NpConfigService;

import java.util.Collection;

/**
 * vt_np_config Service 实现
 */
@Service
public class NpConfigServiceImpl extends ServiceImpl<NpConfigMapper, NpConfig> implements NpConfigService {

    private static final String BIZ_CODE = "npConfigCode";

    @Override
    public NpConfig getByCode(String npConfigCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<NpConfig>().eq(StrUtil.toUnderlineCase(BIZ_CODE), npConfigCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(NpConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notBlank(code == null ? null : code.toString(), "np_config_code 不能为空");
        Assert.isNull(getByCode(code.toString()), "二要素配置编码已存在");
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(NpConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "np_config_code 不能为空");
        NpConfig existing = getByCode(code.toString());
        Assert.notNull(existing, "二要素配置不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "配置编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> npConfigCodes) {
        Assert.notEmpty(npConfigCodes, "请选择要删除的配置");
        for (String code : npConfigCodes) {
            NpConfig row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
