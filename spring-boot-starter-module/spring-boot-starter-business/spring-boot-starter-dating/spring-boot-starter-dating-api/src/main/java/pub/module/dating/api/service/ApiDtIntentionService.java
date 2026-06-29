package pub.module.dating.api.service;

import java.util.Collection;
import java.util.Map;

import pub.module.dating.api.service.dto.DtIntentionDTO;

public interface ApiDtIntentionService {
    DtIntentionDTO getDtIntention(String intentionUserCode);

    /** 仅查询已有意向，不自动初始化默认记录 */
    DtIntentionDTO findDtIntentionIfPresent(String intentionUserCode);

    /** 批量查询已有意向（不自动初始化），key 为 intentionUserCode */
    Map<String, DtIntentionDTO> findDtIntentionByUserCodes(Collection<String> intentionUserCodes);
}
