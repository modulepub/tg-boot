package pub.module.dating.biz.service;

import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.entity.DtMatch;

import java.util.List;

/**
 * 婚恋域微信小程序订阅消息通知。
 */
public interface DatingWxSubscribeNotifyService {

    void sendFriendRequestReceived(DtContactApply apply);

    void sendFriendAddSuccess(DtContactApply apply);

    void sendMatchRequest(DtMatch match, String matchmakerUserCode);

    /** 免费推荐成功后通知用户查看推荐列表 */
    void sendFreeRecommendNotify(String userCode, List<DtCustomerDTO> recommendedCustomers);
}
