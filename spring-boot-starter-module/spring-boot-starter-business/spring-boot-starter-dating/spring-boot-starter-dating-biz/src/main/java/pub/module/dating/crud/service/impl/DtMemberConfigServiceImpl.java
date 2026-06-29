package pub.module.dating.crud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.crud.entity.DtMemberConfig;
import pub.module.dating.crud.mapper.DtMemberConfigMapper;
import pub.module.dating.crud.service.DtMemberConfigService;

/**
 * 婚恋系统-会员配置 Service
 */
@Slf4j
@Service
public class DtMemberConfigServiceImpl extends ServiceImpl<DtMemberConfigMapper, DtMemberConfig>
        implements DtMemberConfigService {

    @Override
    public DtMemberConfig getOrInitConfig() {
        DtMemberConfig config = getOne(
                new QueryWrapper<DtMemberConfig>().orderByAsc("create_time").last("LIMIT 1"), false);
        if (config == null) {
            config = new DtMemberConfig();
            config.setCfgRegisterGiftFreevipStatusCode(StatusCodeEnum.NO);
            save(config);
        }
        return config;
    }

    @Override
    public boolean isRegisterGiftEnabled() {
        return StatusCodeEnum.isYesValue(getOrInitConfig().getCfgRegisterGiftFreevipStatusCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtMemberConfig saveRegisterGiftStatus(StatusCodeEnum statusCode) {
        DtMemberConfig config = getOrInitConfig();
        config.setCfgRegisterGiftFreevipStatusCode(
                statusCode == StatusCodeEnum.YES ? StatusCodeEnum.YES : StatusCodeEnum.NO);
        updateById(config);
        return config;
    }
}
