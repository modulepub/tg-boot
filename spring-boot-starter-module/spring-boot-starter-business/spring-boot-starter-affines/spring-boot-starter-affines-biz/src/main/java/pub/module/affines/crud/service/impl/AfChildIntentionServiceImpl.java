package pub.module.affines.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.affines.crud.entity.AfChildIntention;
import pub.module.affines.crud.mapper.AfChildIntentionMapper;
import pub.module.affines.crud.service.AfChildIntentionService;

import java.util.Collection;

@Service
public class AfChildIntentionServiceImpl extends ServiceImpl<AfChildIntentionMapper, AfChildIntention>
        implements AfChildIntentionService {

    private static final String BIZ_CODE_FIELD = "afChildIntentionCode";
    private static final String BIZ_CODE_PREFIX = "ACI";

    @Override
    public AfChildIntention getByCode(String afChildIntentionCode) {
        if (StrUtil.isBlank(afChildIntentionCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<AfChildIntention>().eq(StrUtil.toUnderlineCase(BIZ_CODE_FIELD), afChildIntentionCode.trim()),
                false);
    }

    @Override
    public AfChildIntention getByChildProfileCode(String afChildProfileCode) {
        if (StrUtil.isBlank(afChildProfileCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<AfChildIntention>().eq("af_child_profile_code", afChildProfileCode.trim()),
                false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AfChildIntention entity) {
        ensureBizCode(entity);
        getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AfChildIntention entity) {
        if (StrUtil.isNotBlank(entity.getAfChildIntentionCode())) {
            AfChildIntention existing = getByCode(entity.getAfChildIntentionCode());
            if (existing != null) {
                entity.setId(existing.getId());
            }
        }
        getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> afChildIntentionCodes) {
        if (afChildIntentionCodes == null || afChildIntentionCodes.isEmpty()) {
            return true;
        }
        for (String code : afChildIntentionCodes) {
            AfChildIntention row = getByCode(code);
            if (row != null) {
                removeById(row.getId());
            }
        }
        return true;
    }

    private static void ensureBizCode(AfChildIntention entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE_FIELD);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE_FIELD, BIZ_CODE_PREFIX + IdUtil.getSnowflakeNextIdStr());
        }
    }
}
