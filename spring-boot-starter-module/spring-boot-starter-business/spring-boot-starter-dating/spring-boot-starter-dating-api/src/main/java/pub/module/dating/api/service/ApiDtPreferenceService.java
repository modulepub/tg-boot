package pub.module.dating.api.service;


/**
 * Api 偏好 Service
 *
 * @author tg
 * 2026-03-31 02:10:33
 */
public interface ApiDtPreferenceService  {

    void saveOrUpdate(String preferenceCusCode, String preferenceTargetCusCode, String preferenceLikeStatusCode);

    /** 资料完善后，重新核算该用户已发出的喜欢是否构成双向喜欢 */
    void refreshOutgoingLikesMutualStatus(String preferenceCusCode);

}
