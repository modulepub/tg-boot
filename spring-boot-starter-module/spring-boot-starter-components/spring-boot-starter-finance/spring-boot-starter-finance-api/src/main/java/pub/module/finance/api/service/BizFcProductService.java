package pub.module.finance.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品管理
 * @author tg
 * @since 2025-10-11
 * @version V1.0
 */
public interface BizFcProductService  {
    List<GetPeriodByProductCodeResDTO> getGetPeriodsByProductCode(String fcProductCode, BigDecimal fcLoanApyAmount);

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description="金融产品期数")
    class GetPeriodByProductCodeResDTO {
        @Schema(description = "名称")
        private String name;
        @Schema(description = "期数")
        private Long periods;
        @Schema(description = "还款金额")
        private BigDecimal repayAmount;
        @Schema(description = "利息")
        private BigDecimal interestAmount;
        @Schema(description = "折扣")
        private BigDecimal discount;
    }
}
