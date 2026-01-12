package pub.module.finance.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pub.module.finance.curd.entity.FcCreditExtension;

@Getter
public class FcCreditExtensionEvent extends ApplicationEvent {
    FcCreditExtension fcCreditExtension;
    public FcCreditExtensionEvent(FcCreditExtension source) {
        super(source);
        this.fcCreditExtension = source;
    }
}
