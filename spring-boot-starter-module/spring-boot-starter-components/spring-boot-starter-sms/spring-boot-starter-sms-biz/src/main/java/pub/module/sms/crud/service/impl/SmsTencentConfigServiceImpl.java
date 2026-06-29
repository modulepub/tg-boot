package pub.module.sms.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.sms.crud.entity.SmsTencentConfig;
import pub.module.sms.crud.mapper.SmsTencentConfigMapper;
import pub.module.sms.crud.service.ISmsTencentConfigService;

import java.util.Collection;

@Service
public class SmsTencentConfigServiceImpl extends ServiceImpl<SmsTencentConfigMapper, SmsTencentConfig>
        implements ISmsTencentConfigService {

    private static final String BIZ_CODE = "smsTencentConfigCode";

    @Override
    public SmsTencentConfig getByCode(String smsTencentConfigCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<SmsTencentConfig>().eq(StrUtil.toUnderlineCase(BIZ_CODE), smsTencentConfigCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SmsTencentConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code != null && StrUtil.isNotBlank(code.toString())) {
            Assert.isNull(getByCode(code.toString()), "腾讯云短信配置编码已存在");
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SmsTencentConfig entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "smsTencentConfigCode 不能为空");
        SmsTencentConfig existing = getByCode(code.toString());
        Assert.notNull(existing, "腾讯云短信配置不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "配置编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> smsTencentConfigCodes) {
        Assert.notEmpty(smsTencentConfigCodes, "请选择要删除的配置");
        for (String code : smsTencentConfigCodes) {
            SmsTencentConfig row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
