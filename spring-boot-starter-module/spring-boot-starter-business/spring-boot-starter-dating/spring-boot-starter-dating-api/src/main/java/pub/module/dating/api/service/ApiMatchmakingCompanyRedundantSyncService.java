package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.MatchmakingCompanyRedundantDTO;

/**
 * 婚介公司资料变更后，同步婚恋模块各表冗余快照。
 */
public interface ApiMatchmakingCompanyRedundantSyncService {

    /**
     * @param company 已落库的最新企业资料快照
     */
    void syncAfterCompanyUpdated(MatchmakingCompanyRedundantDTO company);
}
