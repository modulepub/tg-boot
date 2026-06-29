package pub.module.affines.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.affines.crud.entity.AfChildProfile;
import pub.module.affines.crud.mapper.AfChildProfileMapper;
import pub.module.affines.crud.service.AfChildProfileService;

import java.util.Collection;

@Service
public class AfChildProfileServiceImpl extends ServiceImpl<AfChildProfileMapper, AfChildProfile>
        implements AfChildProfileService {

    private static final String BIZ_CODE_FIELD = "afChildProfileCode";
    private static final String BIZ_CODE_PREFIX = "ACP";

    @Override
    public AfChildProfile getByCode(String afChildProfileCode) {
        if (StrUtil.isBlank(afChildProfileCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<AfChildProfile>().eq(StrUtil.toUnderlineCase(BIZ_CODE_FIELD), afChildProfileCode.trim()),
                false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AfChildProfile entity) {
        ensureBizCode(entity);
        getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AfChildProfile entity) {
        if (StrUtil.isNotBlank(entity.getAfChildProfileCode())) {
            AfChildProfile existing = getByCode(entity.getAfChildProfileCode());
            if (existing != null) {
                entity.setId(existing.getId());
            }
        }
        getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> afChildProfileCodes) {
        if (afChildProfileCodes == null || afChildProfileCodes.isEmpty()) {
            return true;
        }
        for (String code : afChildProfileCodes) {
            AfChildProfile row = getByCode(code);
            if (row != null) {
                removeById(row.getId());
            }
        }
        return true;
    }

    private static void ensureBizCode(AfChildProfile entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE_FIELD);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE_FIELD, BIZ_CODE_PREFIX + IdUtil.getSnowflakeNextIdStr());
        }
    }
}
