package pub.module.dating.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公开展示的已认证婚介公司信息（不含敏感字段）。
 */
@Data
@Schema(description = "公开-已认证婚介公司")
public class MatchmakingCompanyPublicDTO {

    @Schema(description = "婚介公司编码")
    private String mkCompanyCode;

    @Schema(description = "婚介公司名称")
    private String mkCompanyName;

    @Schema(description = "公司电话")
    private String mkCompanyTel;

    @Schema(description = "公司地址")
    private String mkCompanyAddressDetail;

    @Schema(description = "办公照片（逗号分隔）")
    private String mkCompanyPhotos;

    @Schema(description = "入驻审核通过时间")
    private LocalDateTime mkCompanyAuditAt;
}
