package pub.module.finance.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pub.module.finance.curd.entity.FcAccount;

@Getter
public class BindBankCardEvent extends ApplicationEvent {
    FcAccount fcAccount;
    public BindBankCardEvent(FcAccount source) {
        super(source);
        this.fcAccount = source;
    }
}
