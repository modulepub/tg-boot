package pub.module.finance.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 信用借贷管理
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-02
 */
public interface BizFcLoanService {
    /**
     * 现金借贷审核通过，生成分期账单，放款
     * @param fcLoanCode 借贷编码
     */
    void cashLoanPass(String fcLoanCode);

    /**
     * 现金借贷审核拒绝
     *
     * @param fcLoanCode 借贷编码
     */
    void cashLoanReject(String fcLoanCode);

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "借贷申请")
    class FcLoanDTO implements Serializable {
        @Schema(description = "产品编码")
        private String fcProductCode;
        @Schema(description = "商品编码")
        private String mlGoodsCode;
        @Schema(description = "订单编码")
        private String mlOrderCode;
        @Schema(description = "申请金额")
        private java.math.BigDecimal fcLoanApyAmount;
        @Schema(description = "分期期数")
        private Integer fcLoanPeriods;
        @Schema(description = "收款账户编码")
        private String fcAcCode;
        @Schema(description = "借款用途")
        private String fcLoanUseTypeCode;
        @Schema(description = "用户编码")
        private String userCode;

    }

    /**
     * 申请借款
     * @param fcLoanDTO 借贷申请
     */
    void apply(FcLoanDTO fcLoanDTO);



}
