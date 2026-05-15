package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.DtMatchDTO;

/**
 * 匹配申请（牵线）业务 Service
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
public interface ApiDtMatchService {

    /**
     * 当前用户发起牵线：写入牵线记录，并生成一条联系人来源为「红娘牵线」（{@code matchmakerMatching}）的添加好友申请。
     *
     * @param body               请求体（服务端以登录用户为准填充追求者信息）
     * @param applicantUserCode 当前登录用户 {@code userCode}
     */
    DtMatchDTO apply(DtMatchDTO body, String applicantUserCode);
}
