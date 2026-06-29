package pub.module.dating.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.service.ApiDtIntentionService;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.crud.entity.DtIntention;
import pub.module.dating.crud.service.DtIntentionService;
import pub.module.system.api.constants.UserSexCodeEnum;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端推荐意向（交友意向）查询与默认初始化
 */
@Service
public class ApiDtIntentionServiceImpl implements ApiDtIntentionService {

    @Resource
    private DtIntentionService dtIntentionService;

    @Override
    public DtIntentionDTO findDtIntentionIfPresent(String intentionUserCode) {
        if (StrUtil.isBlank(intentionUserCode)) {
            return null;
        }
        DtIntention existing = dtIntentionService.lambdaQuery()
                .eq(DtIntention::getIntentionUserCode, intentionUserCode.trim())
                .orderByDesc(DtIntention::getCreateTime)
                .last("LIMIT 1")
                .one();
        return existing != null ? toDto(existing) : null;
    }

    @Override
    public Map<String, DtIntentionDTO> findDtIntentionByUserCodes(Collection<String> intentionUserCodes) {
        if (intentionUserCodes == null || intentionUserCodes.isEmpty()) {
            return Map.of();
        }
        List<String> codes = intentionUserCodes.stream()
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
        if (codes.isEmpty()) {
            return Map.of();
        }
        List<DtIntention> list = dtIntentionService.lambdaQuery()
                .in(DtIntention::getIntentionUserCode, codes)
                .orderByDesc(DtIntention::getCreateTime)
                .list();
        Map<String, DtIntentionDTO> result = new HashMap<>();
        for (DtIntention row : list) {
            String key = StrUtil.trim(row.getIntentionUserCode());
            if (StrUtil.isBlank(key) || result.containsKey(key)) {
                continue;
            }
            result.put(key, toDto(row));
        }
        return result;
    }

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
        e.setIntentionSexCode(UserSexCodeEnum.WOMAN);
        e.setIntentionHaveHouseCode(null);
        e.setIntentionHaveCarCode(null);
        e.setIntentionDisabledStatusCode(null);
        return e;
    }

    private static DtIntentionDTO toDto(DtIntention entity) {
        return BeanUtil.copyProperties(entity, DtIntentionDTO.class);
    }
}
