package pub.module.trade.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.trade.crud.entity.TdGoodsBenefit;
import pub.module.trade.crud.mapper.TdGoodsBenefitMapper;
import pub.module.trade.crud.service.ITdGoodsBenefitService;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class TdGoodsBenefitServiceImpl extends ServiceImpl<TdGoodsBenefitMapper, TdGoodsBenefit>
        implements ITdGoodsBenefitService {

    private void setDefaultValue(TdGoodsBenefit entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "tdGdBnfCode");
        Assert.notNull(declaredField, "tdGdBnfCode 字段未设置");
        Object code = ReflectUtil.getFieldValue(entity, declaredField);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @Override
    public List<TdGoodsBenefit> listByTdGdCode(String tdGdCode) {
        if (StrUtil.isBlank(tdGdCode)) {
            return Collections.emptyList();
        }
        return lambdaQuery()
                .eq(TdGoodsBenefit::getTdGdCode, tdGdCode.trim())
                .orderByAsc(TdGoodsBenefit::getSeqNo)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceByTdGdCode(String tdGdCode, List<TdGoodsBenefit> benefitList) {
        Assert.notBlank(tdGdCode, "tdGdCode 不能为空");
        deletePhysicalByTdGdCode(tdGdCode);
        if (benefitList == null || benefitList.isEmpty()) {
            return;
        }
        long seqNo = 1L;
        for (TdGoodsBenefit item : benefitList) {
            if (item == null || StrUtil.isBlank(item.getTdGdBnfKey())) {
                continue;
            }
            item.setId(null);
            item.setTdGdBnfCode(null);
            item.setTdGdCode(tdGdCode.trim());
            item.setTdGdBnfKey(item.getTdGdBnfKey().trim());
            item.setSeqNo(seqNo++);
            setDefaultValue(item);
            save(item);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByTdGdCode(String tdGdCode) {
        deletePhysicalByTdGdCode(tdGdCode);
    }

    private void deletePhysicalByTdGdCode(String tdGdCode) {
        if (StrUtil.isBlank(tdGdCode)) {
            return;
        }
        getBaseMapper().deletePhysicalByTdGdCode(tdGdCode.trim());
    }
}
