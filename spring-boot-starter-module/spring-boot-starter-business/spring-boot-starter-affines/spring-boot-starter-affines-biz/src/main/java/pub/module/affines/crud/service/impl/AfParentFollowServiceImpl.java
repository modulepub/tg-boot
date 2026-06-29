package pub.module.affines.crud.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.affines.crud.entity.AfParentFollow;
import pub.module.affines.crud.mapper.AfParentFollowMapper;
import pub.module.affines.crud.service.AfParentFollowService;

import java.util.Collection;

@Service
public class AfParentFollowServiceImpl extends ServiceImpl<AfParentFollowMapper, AfParentFollow>
        implements AfParentFollowService {

    private static final String BIZ_CODE_FIELD = "afParentFollowCode";
    private static final String BIZ_CODE_PREFIX = "APF";

    @Override
    public AfParentFollow getByCode(String afParentFollowCode) {
        if (StrUtil.isBlank(afParentFollowCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<AfParentFollow>().eq(StrUtil.toUnderlineCase(BIZ_CODE_FIELD), afParentFollowCode.trim()),
                false);
    }

    @Override
    public AfParentFollow getByFollowerAndTarget(String followerUserCode, String targetChildProfileCode) {
        if (StrUtil.isBlank(followerUserCode) || StrUtil.isBlank(targetChildProfileCode)) {
            return null;
        }
        return getBaseMapper().selectOne(
                new QueryWrapper<AfParentFollow>()
                        .eq("af_follower_user_code", followerUserCode.trim())
                        .eq("af_target_child_profile_code", targetChildProfileCode.trim()),
                false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AfParentFollow entity) {
        ensureBizCode(entity);
        getBaseMapper().insert(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AfParentFollow entity) {
        if (StrUtil.isNotBlank(entity.getAfParentFollowCode())) {
            AfParentFollow existing = getByCode(entity.getAfParentFollowCode());
            if (existing != null) {
                entity.setId(existing.getId());
            }
        }
        getBaseMapper().updateById(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizCodes(Collection<String> afParentFollowCodes) {
        if (afParentFollowCodes == null || afParentFollowCodes.isEmpty()) {
            return true;
        }
        for (String code : afParentFollowCodes) {
            AfParentFollow row = getByCode(code);
            if (row != null) {
                removeById(row.getId());
            }
        }
        return true;
    }

    private static void ensureBizCode(AfParentFollow entity) {
        Object code = ReflectUtil.getFieldValue(entity, BIZ_CODE_FIELD);
        if (code == null || StrUtil.isBlank(code.toString())) {
            ReflectUtil.setFieldValue(entity, BIZ_CODE_FIELD, BIZ_CODE_PREFIX + IdUtil.getSnowflakeNextIdStr());
        }
    }
}
