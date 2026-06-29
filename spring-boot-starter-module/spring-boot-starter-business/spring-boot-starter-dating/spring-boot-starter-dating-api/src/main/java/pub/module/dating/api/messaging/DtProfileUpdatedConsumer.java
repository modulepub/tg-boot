package pub.module.dating.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * 客户资料更新 — MQ 全链路契约（dating 域内广播，同步冗余快照）。
 */
@MqChannel(
        destination = DtProfileUpdatedConsumer.DESTINATION,
        producerFunction = DtProfileUpdatedConsumer.PRODUCER_FUNCTION
)
public interface DtProfileUpdatedConsumer extends MqMessageConsumer<DtProfileUpdatedMessage> {

    String DESTINATION = "dating.profile.updated";
    String PRODUCER_FUNCTION = "datingProfileUpdated";

    void onProfileUpdated(DtProfileUpdatedMessage message);

    @MqSubscribe(group = "dating", function = "datingProfileUpdatedSync")
    interface RedundantSync extends DtProfileUpdatedConsumer {
    }
}
