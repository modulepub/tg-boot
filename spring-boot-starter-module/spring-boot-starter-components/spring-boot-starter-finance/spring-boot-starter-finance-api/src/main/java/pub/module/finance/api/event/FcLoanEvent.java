package pub.module.finance.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pub.module.finance.curd.entity.FcLoan;

@Getter
public class FcLoanEvent extends ApplicationEvent {
    FcLoan fcLoan;
    public FcLoanEvent(FcLoan source) {
        super(source);
        this.fcLoan = source;
    }
}
