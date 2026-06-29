package pub.module.affines.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.affines.crud.entity.AfChildProfileView;
import pub.module.affines.crud.mapper.AfChildProfileViewMapper;
import pub.module.affines.crud.service.AfChildProfileViewService;

import java.util.Collection;

@Service
public class AfChildProfileViewServiceImpl extends ServiceImpl<AfChildProfileViewMapper, AfChildProfileView>
        implements AfChildProfileViewService {

    private static final String BIZ_CODE_FIELD = "afChildProfileViewCode";
    private static final String BIZ_CODE_PREFIX = "ACV";

    @Override
    public AfChildProfileView getByCode(String afChildProfileViewCode) {
        if (StrUtil.isBlank(afChildProfileViewCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<AfChildProfileView>().eq(StrUtil.toUnderlineCase(BIZ_CODE_FIELD), afChildProfileViewCode.trim()),
                false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AfChildProfileView entity) {
        ensureBizCode(entity);
        getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> afChildProfileViewCodes) {
        if (afChildProfileViewCodes == null || afChildProfileViewCodes.isEmpty()) {
            return true;
        }
        for (String code : afChildProfileViewCodes) {
            AfChildProfileView row = getByCode(code);
            if (row != null) {
                removeById(row.getId());
            }
        }
        return true;
    }

    private static void ensureBizCode(AfChildProfileView entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE_FIELD);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE_FIELD, BIZ_CODE_PREFIX + IdUtil.getSnowflakeNextIdStr());
        }
    }
}
