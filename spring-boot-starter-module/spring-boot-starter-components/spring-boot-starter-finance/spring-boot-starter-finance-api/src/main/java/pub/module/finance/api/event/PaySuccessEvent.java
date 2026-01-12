package pub.module.finance.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pub.module.finance.curd.entity.FcAccountLog;

@Getter
public class PaySuccessEvent extends ApplicationEvent {
    FcAccountLog fcAccountLog;
    public PaySuccessEvent(FcAccountLog source) {
        super(source);
        this.fcAccountLog = source;
    }
}
