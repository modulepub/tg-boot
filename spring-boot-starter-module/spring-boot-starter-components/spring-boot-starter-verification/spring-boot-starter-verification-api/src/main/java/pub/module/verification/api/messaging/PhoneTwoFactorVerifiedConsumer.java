package pub.module.verification.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * 手机号二要素核验完成 — MQ 全链路契约。
 */
@MqChannel(
        destination = PhoneTwoFactorVerifiedConsumer.DESTINATION,
        producerFunction = PhoneTwoFactorVerifiedConsumer.PRODUCER_FUNCTION
)
public interface PhoneTwoFactorVerifiedConsumer extends MqMessageConsumer<PhoneTwoFactorVerifiedMessage> {

    String DESTINATION = "verification.phone-two-factor.verified";
    String PRODUCER_FUNCTION = "verificationPhoneTwoFactorVerified";

    void onPhoneTwoFactorVerified(PhoneTwoFactorVerifiedMessage message);

    @MqSubscribe(group = "dating", function = "datingPhoneTwoFactorVerified")
    interface Dating extends PhoneTwoFactorVerifiedConsumer {
    }
}
