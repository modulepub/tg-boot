package pub.module.file.api.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.File;


public interface BizOcrService {

    @Schema(description = "银行卡识别")
    @Data
    class BankOcr {
        @Schema(description = "银行名称")
        String bankName;
        @Schema(description = "银行卡号")
        String bankCardNo;

    }

    BankOcr bankOcr(File file);

    @Data
    @Schema(description = "身份证识别")
    class IdCardOcr {
        @Schema(description = "姓名")
        String realName;
        @Schema(description = "身份证号")
        String idCardNo;

    }

    IdCardOcr IdCardOcr(File file);
}
