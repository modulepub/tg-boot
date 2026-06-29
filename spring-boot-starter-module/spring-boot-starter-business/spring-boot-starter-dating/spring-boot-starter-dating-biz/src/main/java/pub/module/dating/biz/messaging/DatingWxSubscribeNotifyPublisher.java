package pub.module.dating.biz.messaging;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.TransactionAfterCommit;
import pub.module.dating.biz.service.DatingWxSubscribeNotifyService;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.entity.DtMatch;

@Service
public class DatingWxSubscribeNotifyPublisher {

    @Resource
    private DatingWxSubscribeNotifyService datingWxSubscribeNotifyService;

    public void publishFriendRequestAfterCommit(DtContactApply apply) {
        TransactionAfterCommit.runAfterCommit(() -> datingWxSubscribeNotifyService.sendFriendRequestReceived(apply));
    }

    public void publishFriendAddSuccessAfterCommit(DtContactApply apply) {
        TransactionAfterCommit.runAfterCommit(() -> datingWxSubscribeNotifyService.sendFriendAddSuccess(apply));
    }

    public void publishMatchRequestAfterCommit(DtMatch match, String matchmakerUserCode) {
        TransactionAfterCommit.runAfterCommit(
                () -> datingWxSubscribeNotifyService.sendMatchRequest(match, matchmakerUserCode));
    }
}
