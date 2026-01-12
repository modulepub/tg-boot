package pub.module.ocr.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


public interface BizBankInfoService {

    @Schema(description = "银行卡信息返回")
    @Data
    class BankInfo {
        @Schema(description = "银行名称")
        String bankName;
        @Schema(description = "银行LOGO")
        String bankLogo;
        @Schema(description = "银行编码")
        String bankCode;

    }

    /**
     * 获取银行信息通过银行卡
     * @param bankCardNo 银行卡
     * @return 银行信息
     */
    BankInfo getBankInfoByBankCardNo(String bankCardNo);
}
