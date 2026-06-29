package pub.module.system.crud.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pub.module.system.crud.entity.SysUserBadge;
import pub.module.system.crud.mapper.SysUserBadgeMapper;
import pub.module.system.crud.service.SysUserBadgeService;

import java.util.Collections;
import java.util.List;

@Service
public class SysUserBadgeServiceImpl extends ServiceImpl<SysUserBadgeMapper, SysUserBadge>
        implements SysUserBadgeService {

    @Override
    public SysUserBadge getByUserCodeAndBadgeKey(String userCode, String badgeKey) {
        if (StrUtil.hasBlank(userCode, badgeKey)) {
            return null;
        }
        return getOne(new QueryWrapper<SysUserBadge>().lambda()
                .eq(SysUserBadge::getUserCode, userCode.trim())
                .eq(SysUserBadge::getBadgeKey, badgeKey.trim()), false);
    }

    @Override
    public List<SysUserBadge> listByUserCode(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return Collections.emptyList();
        }
        return list(new QueryWrapper<SysUserBadge>().lambda()
                .eq(SysUserBadge::getUserCode, userCode.trim()));
    }
}
