package pub.module.distribution.api.service;

import pub.module.distribution.api.service.dto.DistPromoteInviteeRowDTO;
import pub.module.distribution.api.service.dto.DistPromoteSummaryDTO;

import java.util.List;

/**
 * 用户端推广页 API。
 */
public interface ApiDistPromoteService {

    DistPromoteSummaryDTO getSummary(String promoterUserCode, String distBizLineCode);

    List<DistPromoteInviteeRowDTO> listInvitees(String promoterUserCode, String distBizLineCode);
}
