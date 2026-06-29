package pub.module.dating.api.service;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.dating.api.constants.ContactSourceCodeEnum;
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
        private ContactSourceCodeEnum contactSourceCode;
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

    /**
     * 双方相互喜欢时，自动建立双向联系人（来源：相互喜欢）。
     * 若已是好友则跳过；若存在待处理申请则标记通过并统一来源。
     */
    void ensureMutualLikeContacts(String cusCodeA, String cusCodeB);

    /**
     * 按用户编码建立双向联系人并同步 IM 好友关系。
     * 若已是双向联系人则跳过。
     */
    void ensureMutualContactsByUserCode(String userCodeA, String userCodeB, ContactSourceCodeEnum source, String greeting);


}
