package pub.module.sms.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.sms.crud.entity.SmsSendLog;
import pub.module.sms.crud.mapper.SmsSendLogMapper;
import pub.module.sms.crud.service.ISmsSendLogService;

import java.lang.reflect.Field;

@Service
public class SmsSendLogServiceImpl extends ServiceImpl<SmsSendLogMapper, SmsSendLog> implements ISmsSendLogService {

    private static final String BIZ_CODE = "smsSendLogCode";

    private void setDefaultValue(SmsSendLog entity) {
        Field field = ReflectUtil.getField(entity.getClass(), BIZ_CODE);
        Assert.notNull(field, "业务编码字段未配置");
        Object code = ReflectUtil.getFieldValue(entity, field);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, field, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public SmsSendLog getByCode(String smsSendLogCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<SmsSendLog>().eq(StrUtil.toUnderlineCase(BIZ_CODE), smsSendLogCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(SmsSendLog entity) {
        setDefaultValue(entity);
        return getBaseMapper().insert(entity) > 0;
    }
}
