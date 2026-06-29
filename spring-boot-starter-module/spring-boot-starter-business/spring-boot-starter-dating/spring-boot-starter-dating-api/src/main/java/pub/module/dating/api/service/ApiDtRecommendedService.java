package pub.module.dating.api.service;


import pub.module.dating.api.service.dto.DtIntentionDTO;

/**
 * Api 对象推荐 Service
 *
 * @author tg
 * 2026-03-30 00:52:26
 */
public interface ApiDtRecommendedService {

    /**
     * 同步免费推荐：校验今日是否已达 {@code recommendCount}，未达标则按差额查询并写入。
     *
     * @param recommendCount 今日推荐数量上限
     */
    void synFreeRecommend(DtIntentionDTO dtIntentionDTO, String userCode, int recommendCount);

    /**
     * 同步免费推荐；{@code leadCusCode} 非空时优先写入该嘉宾并置顶于当日推荐。
     */
    void synFreeRecommend(DtIntentionDTO dtIntentionDTO, String userCode, int recommendCount, String leadCusCode);

}
