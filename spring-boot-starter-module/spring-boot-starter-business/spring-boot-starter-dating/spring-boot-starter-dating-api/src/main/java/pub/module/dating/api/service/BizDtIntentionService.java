package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.DtIntentionDTO;

/**
 * 相亲意向
 * @author tg
 * @since 2025-05-29
 * @version V1.0
 */
public interface BizDtIntentionService{
    DtIntentionDTO getLastIntention(String userCode);
    DtIntentionDTO initDtIntention(DtIntentionDTO dtIntention);

}
