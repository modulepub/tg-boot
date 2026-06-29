package pub.module.distribution.biz.messaging;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.service.ApiDistUserBillSummaryService;
import pub.module.system.api.messaging.SysUserRegisteredMessage;
import pub.module.system.api.messaging.SystemUserRegisteredConsumer;

/**
 * 订阅用户注册成功，初始化分销用户账单汇总。
 */
@Slf4j
@Component
public class DistributionSystemUserRegisteredHandler implements SystemUserRegisteredConsumer.Distribution {

    @Resource
    private ApiDistUserBillSummaryService apiDistUserBillSummaryService;

    @Override
    public void onUserRegistered(SysUserRegisteredMessage message) {
        if (message == null || message.getUser() == null || StrUtil.isBlank(message.getUser().getUserCode())) {
            return;
        }
        try {
            apiDistUserBillSummaryService.initOnUserRegistered(
                    message.getUser(), DistBizLineCodeEnum.DATING.getCode());
        } catch (Exception ex) {
            log.warn("注册后初始化用户账单汇总失败 userCode={}: {}",
                    message.getUser().getUserCode(), ex.getMessage());
            throw ex;
        }
    }
}
