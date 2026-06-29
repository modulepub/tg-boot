package pub.module.verification.biz.messaging;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.messaging.MqPublisher;
import pub.module.verification.api.dto.PhoneTwoFactorChannelOutcome;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyRequest;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;
import pub.module.verification.api.messaging.PhoneTwoFactorVerifiedConsumer;
import pub.module.verification.api.messaging.PhoneTwoFactorVerifiedMessage;

/**
 * 手机号二要素核验完成消息发布（事务提交后发送）。
 */
@Slf4j
@Service
public class PhoneTwoFactorVerifiedPublisher {

    @Resource
    private MqPublisher mqPublisher;

    public void publishAfterCommit(PhoneTwoFactorVerifyRequest request,
                                   PhoneTwoFactorChannelOutcome outcome,
                                   PhoneTwoFactorVerifyResult result) {
        if (result == null) {
            return;
        }
        PhoneTwoFactorVerifiedMessage message = PhoneTwoFactorVerifiedMessage.builder()
                .npRecordUserCode(request == null ? null : request.getNpRecordUserCode())
                .npRecordUserPhone(request == null ? null : request.getNpRecordUserPhone())
                .vendorBizCode(outcome == null ? null : outcome.getVendorBizCode())
                .result(result)
                .build();
        mqPublisher.publishAfterCommit(PhoneTwoFactorVerifiedConsumer.class, message);
    }
}
