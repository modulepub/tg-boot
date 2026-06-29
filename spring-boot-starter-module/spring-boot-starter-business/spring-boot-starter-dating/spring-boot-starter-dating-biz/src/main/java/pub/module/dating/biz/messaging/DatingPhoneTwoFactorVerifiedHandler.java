package pub.module.dating.biz.messaging;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;
import pub.module.verification.api.messaging.PhoneTwoFactorVerifiedConsumer;
import pub.module.verification.api.messaging.PhoneTwoFactorVerifiedMessage;

/**
 * 订阅二要素核验完成消息，更新客户实名状态。
 */
@Slf4j
@Component
public class DatingPhoneTwoFactorVerifiedHandler implements PhoneTwoFactorVerifiedConsumer.Dating {

    /** 与前端 / {@link pub.module.verification.api.dto.PhoneTwoFactorVerifyRequest} 示例一致 */
    public static final String SOURCE_MODULE_CUSTOMER = "customer";

    private static final String SOURCE_MODULE_CUSTOMER_LEGACY = "DtCustomer";

    private static final String BIZ_CODE_CONSISTENT = "1";

    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Override
    public void onPhoneTwoFactorVerified(PhoneTwoFactorVerifiedMessage message) {
        if (message == null || message.getResult() == null) {
            return;
        }
        PhoneTwoFactorVerifyResult persisted = message.getResult();
        if (!isCustomerSourceModule(persisted.getNpRecordSourceModuleCode())) {
            return;
        }
        String vendorBizCode = StrUtil.trim(message.getVendorBizCode());
        if (!BIZ_CODE_CONSISTENT.equals(vendorBizCode)
                || !BIZ_CODE_CONSISTENT.equals(StrUtil.trim(persisted.getNpRecordPassedStatusCode()))) {
            log.info("跳过客户实名更新：BizCode={} 非核验一致", vendorBizCode);
            return;
        }

        String userCode = StrUtil.trim(message.getNpRecordUserCode());
        if (StrUtil.isBlank(userCode)) {
            log.warn("跳过客户实名更新：npRecordUserCode 为空 npRecordCode={}", persisted.getNpRecordCode());
            return;
        }

        String reqPhone = StrUtil.trim(persisted.getNpRecordPhone());
        String userPhone = StrUtil.trim(message.getNpRecordUserPhone());
        if (StrUtil.isBlank(reqPhone) || StrUtil.isBlank(userPhone) || !reqPhone.equals(userPhone)) {
            log.warn("跳过客户实名更新：核验手机号与用户信息不一致 userCode={}", userCode);
            return;
        }

        String recordBizCode = StrUtil.trim(persisted.getNpRecordBizCode());
        if (StrUtil.isNotBlank(recordBizCode)) {
            DtCustomerDTO customer = apiDtCustomerService.getCusByUserCode(userCode);
            String cusCode = customer == null ? null : StrUtil.trim(customer.getCusCode());
            if (StrUtil.isBlank(cusCode) || !recordBizCode.equals(cusCode)) {
                log.warn("跳过客户实名更新：npRecordBizCode 与当前客户 cusCode 不一致 userCode={}", userCode);
                return;
            }
        }

        boolean updated = apiDtCustomerService.applyIdentityAfterPhoneTwoFactorVerify(
                userCode, reqPhone, StrUtil.trim(persisted.getNpRecordRealName()));
        if (updated) {
            log.info("已通过 MQ 更新客户实名 userCode={}", userCode);
        }
    }

    private static boolean isCustomerSourceModule(String sourceModuleCode) {
        String code = StrUtil.trim(sourceModuleCode);
        return SOURCE_MODULE_CUSTOMER.equalsIgnoreCase(code)
                || SOURCE_MODULE_CUSTOMER_LEGACY.equals(code);
    }
}
