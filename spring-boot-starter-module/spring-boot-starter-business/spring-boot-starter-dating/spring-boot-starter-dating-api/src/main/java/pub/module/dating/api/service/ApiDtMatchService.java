package pub.module.dating.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pub.module.dating.api.service.dto.DtMatchDTO;
import pub.module.dating.api.service.dto.DtMatchUpdateRelationProgressVO;

/**
 * 匹配申请（牵线）业务 Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
public interface ApiDtMatchService {

    /**
     * 当前用户发起牵线：仅写入牵线记录并通知红娘，与好友/联系人申请无关联。
     *
     * @param body               请求体（服务端以登录用户为准填充追求者信息）
     * @param applicantUserCode 当前登录用户 {@code userCode}
     */
    DtMatchDTO apply(DtMatchDTO body, String applicantUserCode);

    /**
     * 红娘工作台：分页查询归属当前红娘的牵线申请，并补充双方手机号与好友关系。
     *
     * @param matchmakerUserCode 当前登录红娘 system userCode
     */
    IPage<DtMatchDTO> listForMatchmaker(String matchmakerUserCode, Integer pageNo, Integer pageSize);

    /**
     * 红娘更新牵线关系进度及截图。
     *
     * @param vo                 更新内容
     * @param matchmakerUserCode 当前登录红娘 system userCode
     */
    DtMatchDTO updateRelationProgress(DtMatchUpdateRelationProgressVO vo, String matchmakerUserCode);
}
