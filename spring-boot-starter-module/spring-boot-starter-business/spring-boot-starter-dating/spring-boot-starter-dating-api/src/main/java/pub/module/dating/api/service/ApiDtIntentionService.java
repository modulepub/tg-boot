package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.DtIntentionDTO;

public interface ApiDtIntentionService {
    DtIntentionDTO getDtIntention(String intentionUserCode);
}
