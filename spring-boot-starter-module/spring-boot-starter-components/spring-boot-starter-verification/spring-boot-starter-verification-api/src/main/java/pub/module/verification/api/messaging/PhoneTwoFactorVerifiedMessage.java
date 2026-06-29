package pub.module.verification.api.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pub.module.verification.api.dto.PhoneTwoFactorVerifyResult;

import java.io.Serializable;

/**
 * 手机号二要素核验完成消息（事务落库后发布，供 customer 等模块异步处理）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneTwoFactorVerifiedMessage implements Serializable {

    /** 发起方用户编码（HTTP 请求上下文或入参携带） */
    private String npRecordUserCode;

    /** 发起方用户手机号（用于与核验手机号交叉校验） */
    private String npRecordUserPhone;

    /** 阿里云 BizCode：1 一致 2 不一致 3 查无 */
    private String vendorBizCode;

    private PhoneTwoFactorVerifyResult result;
}
