package pub.module.dating.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;

/**
 * 红娘信息 对象
 *
 * @author tg
 * 2026-03-22 13:32:45
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "红娘信息")
public class DtMatchmakingCompany extends BaseEntity {
    @Schema(description = "婚介所编码")
    private String mkCompanyCode;
    @Schema(description = "婚介所名称")
    private String mkCompanyName;

    @Schema(description = "公司电话")
    private String mkCompanyTel;
    @Schema(description = "执照号")
    private String mkCompanyUsciCode;
    @Schema(description = "法人姓名")
    private String mkCompanyLegalName;
    @Schema(description = "法人证件号")
    private String mkCompanyLegalIdNo;
    @Schema(description = "公司认证状态")
    private String mkCompanyIdentityStatusCode;
    @Schema(description = "公司地址")
    private String mkCompanyAddressDetail;
    @Schema(description = "公司定位")
    private String mkCompanyAddressLatLon;
    @Schema(description = "办公照片")
    private String mkCompanyPhotos;

}
