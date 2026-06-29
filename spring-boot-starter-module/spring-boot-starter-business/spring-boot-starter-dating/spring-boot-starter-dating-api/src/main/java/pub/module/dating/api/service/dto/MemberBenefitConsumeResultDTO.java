package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "会员权益消费结果")
public class MemberBenefitConsumeResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否消费成功")
    private boolean success;

    @Schema(description = "失败错误码")
    private String errorCode;

    @Schema(description = "失败说明")
    private String errorMessage;

    public static MemberBenefitConsumeResultDTO ok() {
        MemberBenefitConsumeResultDTO dto = new MemberBenefitConsumeResultDTO();
        dto.setSuccess(true);
        return dto;
    }

    public static MemberBenefitConsumeResultDTO fail(String errorCode, String errorMessage) {
        MemberBenefitConsumeResultDTO dto = new MemberBenefitConsumeResultDTO();
        dto.setSuccess(false);
        dto.setErrorCode(errorCode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }
}
