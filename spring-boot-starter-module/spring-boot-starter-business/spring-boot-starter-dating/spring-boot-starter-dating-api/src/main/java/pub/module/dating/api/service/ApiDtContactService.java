package pub.module.dating.api.service;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Api 联系人 Service
 *
 * @author tg
 * 2026-05-01 23:01:09
 */
public interface ApiDtContactService  {
    @Data
    class RemoveDTO{
        @Schema(description = "编码")
        private String contactCode;
    }
    void remove(RemoveDTO removeDTO);
}
