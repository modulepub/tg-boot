package pub.module.dating.api.messaging;

import pub.module.common.messaging.MqChannel;
import pub.module.common.messaging.MqMessageConsumer;
import pub.module.common.messaging.MqSubscribe;

/**
 * 婚介公司资料更新 — MQ 全链路契约（dating 域内广播，同步冗余快照）。
 */
@MqChannel(
        destination = DtMatchmakingCompanyUpdatedConsumer.DESTINATION,
        producerFunction = DtMatchmakingCompanyUpdatedConsumer.PRODUCER_FUNCTION
)
public interface DtMatchmakingCompanyUpdatedConsumer extends MqMessageConsumer<DtMatchmakingCompanyUpdatedMessage> {

    String DESTINATION = "dating.matchmaking-company.updated";
    String PRODUCER_FUNCTION = "datingMatchmakingCompanyUpdated";

    void onCompanyUpdated(DtMatchmakingCompanyUpdatedMessage message);

    @MqSubscribe(group = "dating", function = "datingMatchmakingCompanyUpdatedSync")
    interface RedundantSync extends DtMatchmakingCompanyUpdatedConsumer {
    }
}
