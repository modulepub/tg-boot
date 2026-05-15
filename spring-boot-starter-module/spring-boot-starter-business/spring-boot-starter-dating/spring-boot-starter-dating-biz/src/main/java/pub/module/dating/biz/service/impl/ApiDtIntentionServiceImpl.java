package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.service.ApiDtIntentionService;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.curd.entity.DtIntention;
import pub.module.dating.curd.service.DtIntentionService;

/**
 * 用户端推荐意向（交友意向）查询与默认初始化
 */
@Service
public class ApiDtIntentionServiceImpl implements ApiDtIntentionService {

    /** 与前端约定：女 */
    private static final String DEFAULT_SEX_FEMALE = "2";

    @Resource
    private DtIntentionService dtIntentionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DtIntentionDTO getDtIntention(String intentionUserCode) {
        if (StrUtil.isBlank(intentionUserCode)) {
            throw new RuntimeException("系统异常");
        }
        DtIntention existing = dtIntentionService.lambdaQuery()
                .eq(DtIntention::getIntentionUserCode, intentionUserCode)
                .orderByDesc(DtIntention::getCreateTime)
                .last("LIMIT 1")
                .one();
        if (existing != null) {
            return toDto(existing);
        }else {
            DtIntention created = buildDefault(intentionUserCode);
            dtIntentionService.save(created);
            return BeanUtil.copyProperties(created,DtIntentionDTO.class);
        }
    }

    private static DtIntention buildDefault(String intentionUserCode) {
        DtIntention e = new DtIntention();
        e.setIntentionUserCode(intentionUserCode);
        e.setIntentionName("默认推荐意向");
        e.setIntentionMinAge(18);
        e.setIntentionMaxAge(60);
        e.setIntentionCityCode(null);
        e.setIntentionSexCode(DEFAULT_SEX_FEMALE);
        e.setIntentionHaveHouseCode(null);
        e.setIntentionHaveCarCode(null);
        e.setIntentionDisabledStatusCode(null);
        return e;
    }

    private static DtIntentionDTO toDto(DtIntention entity) {
        return BeanUtil.copyProperties(entity, DtIntentionDTO.class);
    }
}
