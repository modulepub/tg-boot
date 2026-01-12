package pub.module.finance.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 授信表
 * @author tg
 * @since 2025-11-02
 * @version V1.0
 */
public interface BizFcCreditExtensionService  {
    /**
     * 授信审核通过
     *
     * @param fcCdExCode 借贷编码
     */
    void creditLoanPass(String fcCdExCode);

    /**
     * 授信审核拒绝
     *
     * @param fcCdExCode 借贷编码
     */
    void creditLoanReject(String fcCdExCode);

    void credit(CreditApplyDTO creditApplyDTO);

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "授信申请")
    class CreditApplyDTO implements Serializable {
        @Schema(description = "产品编码，多个以逗号分隔")
        private String fcProductCode;
        @Schema(description = "用户编码")
        private String userCode;

    }
}
