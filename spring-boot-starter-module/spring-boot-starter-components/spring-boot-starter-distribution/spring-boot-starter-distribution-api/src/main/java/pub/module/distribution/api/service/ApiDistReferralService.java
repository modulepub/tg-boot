package pub.module.distribution.api.service;


import pub.module.system.api.service.dto.UserDTO;

/**
 * 推荐关系绑定 API。
 */
public interface ApiDistReferralService {

    /**
     * 新用户注册后绑定推荐关系（幂等）。
     */
    void bindInviteeOnRegister(UserDTO invitee, String inviterUserCode, String distBizLineCode);
}
