package pub.module.finance.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pub.module.finance.curd.entity.FcAccountLog;

@Getter
public class PayFailEvent extends ApplicationEvent {
    FcAccountLog fcAccountLog;
    public PayFailEvent(FcAccountLog source) {
        super(source);
        this.fcAccountLog = source;
    }
}
