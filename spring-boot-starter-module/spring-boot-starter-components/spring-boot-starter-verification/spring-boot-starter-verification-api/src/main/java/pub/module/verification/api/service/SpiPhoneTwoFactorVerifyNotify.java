package pub.module.verification.api.service;

import pub.module.verification.api.dto.PhoneTwoFactorChannelOutcome;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;

/**
 * 三方模块插件：在手机号二要素核验完成并落库后接收通知（仅依赖 verification-api 即可实现）。
 */
public interface SpiPhoneTwoFactorVerifyNotify {

    default int getOrder() {
        return 0;
    }

    /**
     * @param npRecordSourceModuleCode 发起方业务模块编码（与请求入参一致）
     * @param channelOutcome           渠道返回快照
     * @param persisted                已落库的核验结果
     */
    void onVerified(String npRecordSourceModuleCode, PhoneTwoFactorChannelOutcome channelOutcome,
                    PhoneTwoFactorVerifyResult persisted);
}
