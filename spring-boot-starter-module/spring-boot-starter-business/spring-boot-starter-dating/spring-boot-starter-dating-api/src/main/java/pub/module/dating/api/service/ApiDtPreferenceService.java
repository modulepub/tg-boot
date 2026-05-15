package pub.module.dating.api.service;


/**
 * Api 偏好 Service
 *
 * @author tg
 * 2026-03-31 02:10:33
 */
public interface ApiDtPreferenceService  {

    void saveOrUpdate(String preferenceCusCode, String preferenceTargetCusCode, String preferenceLikeStatusCode);

}
