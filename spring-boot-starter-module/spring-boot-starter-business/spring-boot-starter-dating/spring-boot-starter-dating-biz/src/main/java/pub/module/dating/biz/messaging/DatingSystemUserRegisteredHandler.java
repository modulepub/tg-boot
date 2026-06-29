package pub.module.dating.biz.messaging;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.dating.api.constants.CusMemberTierConstants;
import pub.module.dating.crud.service.DtMemberConfigService;
import pub.module.system.api.messaging.SysUserRegisteredMessage;
import pub.module.system.api.messaging.SystemUserRegisteredConsumer;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.trade.api.service.ApiTdOrderService;

import java.math.BigDecimal;

/**
 * 订阅系统用户注册消息：若婚恋会员配置开启「注册即赠钻石会员·体验7天」，
 * 则复用客户管理赠送会员核心流程（商品编码 freevip）为新用户开通会员。
 */
@Slf4j
@Component
public class DatingSystemUserRegisteredHandler implements SystemUserRegisteredConsumer.Dating {

    @Resource
    private DtMemberConfigService memberConfigService;
    @Resource
    private ApiTdOrderService apiTdOrderService;

    @Override
    public void onUserRegistered(SysUserRegisteredMessage message) {
        if (message == null || message.getUser() == null) {
            return;
        }
        UserDTO user = message.getUser();
        String userCode = StrUtil.trimToNull(user.getUserCode());
        if (userCode == null) {
            return;
        }
        // 仅当后台「注册即赠钻石会员·体验7天」开关开启时赠送
        if (!memberConfigService.isRegisterGiftEnabled()) {
            return;
        }
        try {
            // 与客户管理「赠送会员」一致：商品编码 freevip，直接跳过付费流程完成
            apiTdOrderService.createPaidOrder(
                    CusMemberTierConstants.FREE_VIP,
                    BigDecimal.ONE,
                    userCode,
                    user.getUserRealName(),
                    user.getUserPhone());
            log.info("注册即赠会员成功 userCode={}", userCode);
        } catch (Exception ex) {
            // 赠送失败仅记录日志，不抛出，避免 MQ 重投导致重复赠送
            log.warn("注册即赠会员失败 userCode={}: {}", userCode, ex.getMessage(), ex);
        }
    }
}
