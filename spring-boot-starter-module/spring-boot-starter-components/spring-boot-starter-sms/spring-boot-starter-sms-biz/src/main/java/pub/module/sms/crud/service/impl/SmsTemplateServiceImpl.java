package pub.module.sms.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.sms.crud.entity.SmsTemplate;
import pub.module.sms.crud.mapper.SmsTemplateMapper;
import pub.module.sms.crud.service.ISmsTemplateService;

import java.util.Collection;

@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate> implements ISmsTemplateService {

    private static final String BIZ_CODE = "smsTemplateCode";

    @Override
    public SmsTemplate getByCode(String smsTemplateCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<SmsTemplate>().eq(StrUtil.toUnderlineCase(BIZ_CODE), smsTemplateCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SmsTemplate entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code != null && StrUtil.isNotBlank(code.toString())) {
            Assert.isNull(getByCode(code.toString()), "短信模板编码已存在");
        }
        return getBaseMapper().insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SmsTemplate entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        Assert.notNull(code, "smsTemplateCode 不能为空");
        SmsTemplate existing = getByCode(code.toString());
        Assert.notNull(existing, "短信模板不存在");
        if (StrUtil.isNotBlank(entity.getId())) {
            Assert.isTrue(existing.getId().equals(entity.getId()), "模板编码与主键不匹配");
        } else {
            entity.setId(existing.getId());
        }
        return getBaseMapper().updateById(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> smsTemplateCodes) {
        Assert.notEmpty(smsTemplateCodes, "请选择要删除的模板");
        for (String code : smsTemplateCodes) {
            SmsTemplate row = getByCode(code);
            if (row != null) {
                getBaseMapper().deleteById(row.getId());
            }
        }
        return true;
    }
}
