package pub.module.dating.api.service;


import pub.module.dating.api.service.dto.MatchmakerBriefDTO;
import pub.module.dating.api.service.dto.MatchmakerChannelsDTO;
import pub.module.dating.api.service.dto.MatchmakerQualificationApplyDTO;
import pub.module.dating.api.service.dto.MatchmakerQualificationApplySubmitVO;
import pub.module.dating.api.service.dto.MatchmakingCompanyOptionDTO;

import java.util.List;

/**
 * Api 红娘信息 Service
 */
public interface ApiDtMatchmakerService {

    /**
     * 是否已登记为红娘（按 system userCode）。
     */
    boolean isMatchmakerByUserCode(String userCode);

    /**
     * 将红娘姓名 {@code mkName} 同步到系统用户 {@code userRealName}。
     */
    void syncUserRealNameFromMatchmaker(String userCode, String mkName);

    /** 当前用户红娘资质申请信息 */
    MatchmakerQualificationApplyDTO getMyQualificationApply(String userCode);

    /** 提交红娘资质申请 */
    MatchmakerQualificationApplyDTO submitQualificationApply(String userCode, MatchmakerQualificationApplySubmitVO vo);

    /** 资质申请可选的已审核婚介公司列表 */
    List<MatchmakingCompanyOptionDTO> listCertifiedCompanies();

    /** 当前认证红娘的视频号配置 */
    MatchmakerChannelsDTO getMyChannels(String userCode);

    /** 当前认证红娘修改视频号 */
    MatchmakerChannelsDTO updateMyChannels(String userCode, String mkChannelsFinderUserName);

    /** 当前认证红娘提交视频号审核 */
    MatchmakerChannelsDTO submitMyChannels(String userCode);

    /**
     * 校验红娘（system userCode）是否服务指定客户；不满足时抛出 IllegalArgumentException。
     */
    void assertMkUserServesCustomer(String mkUserCode, String cusCode);

    /** 按 system userCode 查询红娘简要信息，不存在返回 null */
    MatchmakerBriefDTO getMatchmakerBriefByUserCode(String userCode);
}
