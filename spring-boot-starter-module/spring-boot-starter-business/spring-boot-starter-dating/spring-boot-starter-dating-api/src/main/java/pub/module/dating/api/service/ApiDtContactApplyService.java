package pub.module.dating.api.service;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.dating.api.service.dto.DtContactApplyDTO;

/**
 * Api 联系人申请表 Service
 *
 * @author tg
 * 2026-05-03 03:39:43
 */
public interface ApiDtContactApplyService  {
    @Data
    class ApplyDTO{
        @Schema(description = "申请人")
        private String cusCode;
        @Schema(description = "打招呼")
        private String contactApplyGreeting;
        @Schema(description = "联系人来源")
        private String contactSourceCode;
    }
    void apply(ApplyDTO applyDTO,String userCode);

    @Data
    class CheckDTO{
        @Schema(description = "申请人")
        private String cusCode;
    }
    DtContactApplyDTO check(CheckDTO checkDTO, String userCode);

    @Data
    class PassDTO{
        @Schema(description = "编码")
        private String contactApplyCode;
    }


    void pass(PassDTO passDTO);

    @Data
    class RejectDTO{
        @Schema(description = "编码")
        private String contactApplyCode;
    }
    void reject(RejectDTO rejectDTO);


}
