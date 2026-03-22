package pub.module.finance.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.finance.api.dto.FcAccountDTO;
import pub.module.finance.api.dto.FcAccountLogDTO;

/**
 * 金融账户管理
 * @author tg
 * @since 2025-09-30
 * @version V1.0
 */
public interface BizFcAccountService {

    FcAccountDTO getAccount(String userCode, String fcProductCode);
    FcAccountDTO getAccount(String fcAcCode);
    @Data
    @Schema(description = "绑定银行卡发送短信")
    class BindCardSmsDTO {
        @Schema(description = "产品编码")
        private String fcProductCode;
        @Schema(description = "银行编码")
        private String fcBankCode;
        @Schema(description = "银行名称")
        private String fcBankName;
        @Schema(description = "银行LOGO")
        private String fcBankLogo;
        @Schema(description = "银行卡号")
        private String fcBankCardNo;
        @Schema(description = "银行卡绑定手机号")
        private String fcBankCardPhone;
        @Schema(description = "姓名")
        private String fcAcSysUserRealName;
        @Schema(description = "身份证号")
        private String fcAcIdCardNo;
        @Schema(description = "用户名")
        private String fcAcSysUserCode;

    }

    /**
     * 绑定银行卡类账户
     */
    FcAccountDTO bindBankCardSms(BindCardSmsDTO bindCardSmsDTO);

    @Data
    @Schema(description = "绑定银行卡")
    class BindBankCardSureDTO {
        @Schema(description = "账户编码（四要素绑卡确认时候用）")
        private String fcAcCode;
        @Schema(description = "用户名")
        private String fcAcSysUserCode;
        @Schema(description = "验证码1")
        private String fcBankCardAuthCode1;
        @Schema(description = "验证码2")
        private String fcBankCardAuthCode2;
    }

    FcAccountDTO bindBankCardSure(BindBankCardSureDTO bindBankCardSureDTO);

    void bankPay(FcAccountLogDTO fcAccountLog);
}
