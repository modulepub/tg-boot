package pub.module.cms.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.cms.crud.entity.CmsNodeReadRecord;
import java.time.LocalDateTime;
import pub.module.cms.crud.mapper.CmsNodeReadRecordMapper;
import pub.module.cms.crud.service.CmsNodeReadRecordService;

@Slf4j
@Service
public class CmsNodeReadRecordServiceImpl extends ServiceImpl<CmsNodeReadRecordMapper, CmsNodeReadRecord>
        implements CmsNodeReadRecordService {

    @Override
    public CmsNodeReadRecord getRecentByNodeCodeAndIp(String nodeCode, String clientIp, int withinMinutes) {
        if (StrUtil.isBlank(nodeCode) || StrUtil.isBlank(clientIp) || withinMinutes <= 0) {
            return null;
        }
        LocalDateTime since = LocalDateTime.now().minusMinutes(withinMinutes);
        return getBaseMapper().selectOne(new QueryWrapper<CmsNodeReadRecord>()
                .eq("node_code", nodeCode.trim())
                .eq("node_read_record_client_ip", clientIp.trim())
                .eq("deleted", "0")
                .ge("create_time", since)
                .orderByDesc("create_time")
                .last("LIMIT 1"), false);
    }

    @Override
    public CmsNodeReadRecord getBySessionCode(String sessionCode) {
        if (StrUtil.isBlank(sessionCode)) {
            return null;
        }
        return getBaseMapper().selectOne(new QueryWrapper<CmsNodeReadRecord>()
                .eq("node_read_record_session_code", sessionCode.trim())
                .eq("deleted", "0"), false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CmsNodeReadRecord entity) {
        Assert.notNull(entity, "阅读记录不能为空");
        if (StrUtil.isBlank(entity.getNodeReadRecordCode())) {
            entity.setNodeReadRecordCode(IdUtil.getSnowflakeNextIdStr());
        }
        if (StrUtil.isBlank(entity.getNodeReadRecordSessionCode())) {
            entity.setNodeReadRecordSessionCode(IdUtil.getSnowflakeNextIdStr());
        }
        if (entity.getNodeReadRecordProgress() == null) {
            entity.setNodeReadRecordProgress(0);
        }
        getBaseMapper().insert(entity);
        return true;
    }
}
