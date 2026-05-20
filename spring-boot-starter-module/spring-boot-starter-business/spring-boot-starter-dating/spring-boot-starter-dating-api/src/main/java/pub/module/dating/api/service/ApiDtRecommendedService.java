package pub.module.dating.api.service;


import pub.module.dating.api.service.dto.DtIntentionDTO;

/**
 * Api 对象推荐 Service
 *
 * @author tg
 * 2026-03-30 00:52:26
 */
public interface ApiDtRecommendedService {

    void synFreeRecommend(DtIntentionDTO dtIntentionDTO,String userCode);

}
