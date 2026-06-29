package pub.module.ai.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.ai.crud.entity.AiUsageRecord;
import pub.module.ai.crud.mapper.AiUsageRecordMapper;
import pub.module.ai.crud.service.IAiUsageRecordService;

@Service
public class AiUsageRecordServiceImpl extends ServiceImpl<AiUsageRecordMapper, AiUsageRecord> implements IAiUsageRecordService {

    private static final String BIZ_CODE = "aiUsageRecordCode";

    @Override
    public AiUsageRecord getByCode(String aiUsageRecordCode) {
        return getBaseMapper().selectOne(
                new QueryWrapper<AiUsageRecord>().eq(StrUtil.toUnderlineCase(BIZ_CODE), aiUsageRecordCode), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AiUsageRecord entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE, IdUtil.getSnowflakeNextIdStr());
        }
        return getBaseMapper().insert(entity) > 0;
    }
}
