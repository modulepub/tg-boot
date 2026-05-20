package pub.module.plugins.customer.verification;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.verification.api.dto.PhoneTwoFactorChannelOutcome;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;
import pub.module.verification.api.service.SpiPhoneTwoFactorVerifyNotify;

/**
 * 客户模块核验插件：{@code npRecordSourceModuleCode = customer} 且阿里云 BizCode=1 时，更新当前登录用户绑定客户的实名状态。
 */
public class CustomerPhoneTwoFactorVerifyNotify implements SpiPhoneTwoFactorVerifyNotify {

    private static final Logger log = LoggerFactory.getLogger(CustomerPhoneTwoFactorVerifyNotify.class);

    public static final String SOURCE_MODULE_CUSTOMER = "customer";

    /** 阿里云 BizCode：1=校验一致 */
    private static final String BIZ_CODE_CONSISTENT = "1";

    private final ApiCustomerService apiCustomerService;

    public CustomerPhoneTwoFactorVerifyNotify(ApiCustomerService apiCustomerService) {
        this.apiCustomerService = apiCustomerService;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void onVerified(String npRecordSourceModuleCode, PhoneTwoFactorChannelOutcome channelOutcome,
            PhoneTwoFactorVerifyResult persisted) {
        if (!SOURCE_MODULE_CUSTOMER.equals(npRecordSourceModuleCode)) {
            return;
        }
        if (persisted == null) {
            return;
        }
        String vendorBizCode = StrUtil.trim(channelOutcome == null ? null : channelOutcome.getVendorBizCode());
        if (!BIZ_CODE_CONSISTENT.equals(vendorBizCode)
                || !BIZ_CODE_CONSISTENT.equals(StrUtil.trim(persisted.getNpRecordPassedStatusCode()))) {
            log.info("CustomerPhoneTwoFactorVerifyNotify: BizCode={} 非核验一致，不更新实名", vendorBizCode);
            return;
        }

        UserDTO user;
        try {
            user = UserUtil.getCurrentSysUser();
        }
        catch (Exception ex) {
            log.warn("CustomerPhoneTwoFactorVerifyNotify: 获取当前用户失败: {}", ex.getMessage());
            return;
        }
        if (user == null || StrUtil.isBlank(user.getUserCode())) {
            log.warn("CustomerPhoneTwoFactorVerifyNotify: 当前用户为空");
            return;
        }

        String reqPhone = StrUtil.trim(persisted.getNpRecordPhone());
        String userPhone = StrUtil.trim(user.getUserPhone());
        if (StrUtil.isBlank(reqPhone) || StrUtil.isBlank(userPhone) || !reqPhone.equals(userPhone)) {
            log.warn("CustomerPhoneTwoFactorVerifyNotify: 核验手机号与登录用户手机号不一致");
            return;
        }

        String recordBizCode = StrUtil.trim(persisted.getNpRecordBizCode());
        if (StrUtil.isNotBlank(recordBizCode)) {
            CustomerDTO customer = apiCustomerService.getCusByUserCode(user.getUserCode());
            String cusCode = customer == null ? null : StrUtil.trim(customer.getCusCode());
            if (StrUtil.isBlank(cusCode) || !recordBizCode.equals(cusCode)) {
                log.warn("CustomerPhoneTwoFactorVerifyNotify: npRecordBizCode 与当前客户 cusCode 不一致");
                return;
            }
        }

        boolean updated = apiCustomerService.applyIdentityAfterPhoneTwoFactorVerify(
                user.getUserCode(), reqPhone, StrUtil.trim(persisted.getNpRecordRealName()));
        if (updated) {
            log.info("CustomerPhoneTwoFactorVerifyNotify: 已更新客户实名 userCode={}", user.getUserCode());
        }
    }
}
