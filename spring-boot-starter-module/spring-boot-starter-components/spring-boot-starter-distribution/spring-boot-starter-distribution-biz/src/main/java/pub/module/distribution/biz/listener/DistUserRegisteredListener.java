package pub.module.distribution.biz.listener;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.service.ApiDistReferralService;
import pub.module.system.api.event.SysUserRegisteredEvent;

@Component
public class DistUserRegisteredListener {

    @Resource
    private ApiDistReferralService apiDistReferralService;

    @Order(200)
    @EventListener
    public void onUserRegistered(SysUserRegisteredEvent event) {
        if (event == null || event.getUser() == null) {
            return;
        }
        String inviter = StrUtil.trimToNull(event.getUserReferenceUserCode());
        if (inviter == null) {
            inviter = StrUtil.trimToNull(event.getUser().getUserReferenceUserCode());
        }
        if (inviter == null) {
            return;
        }
        apiDistReferralService.bindInviteeOnRegister(event.getUser(), inviter, DistBizLineCodeEnum.DATING.getCode());
    }
}
