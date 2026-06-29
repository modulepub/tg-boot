package pub.module.dating.crud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.IdentityApplyProcessCodeEnum;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Schema(description = "是否已通过企业认证（StatusCode：1是 0否，null视同否）")
    private StatusCodeEnum mkCompanyIdentityStatusCode;

    @Schema(description = "入驻申请流程（ProcessCode）")
    private IdentityApplyProcessCodeEnum mkCompanyIdentityProcessCode;
    @Schema(description = "公司地址")
    private String mkCompanyAddressDetail;
    @Schema(description = "所在城市编码")
    private String mkCompanyCityCode;
    @Schema(description = "所在城市名称")
    private String mkCompanyCityName;
    @Schema(description = "公司定位")
    private String mkCompanyAddressLatLon;
    @Schema(description = "办公照片")
    private String mkCompanyPhotos;
    @Schema(description = "管理员用户编码")
    private String mkCompanyAdminUserCode;
    @Schema(description = "管理员真实姓名（冗余）")
    private String mkCompanyAdminUserRealName;
    @Schema(description = "对公银行账号")
    private String mkCompanyPublicAccountNo;
    @Schema(description = "开户行")
    private String mkCompanyBankName;
    @Schema(description = "开户地")
    private String mkCompanyBankLocation;
    @Schema(description = "随机认证金额（元，1元以内）")
    private BigDecimal mkCompanyVerifyAmount;
    @Schema(description = "免再次认证转账：1是")
    private String mkCompanyVerifySkipCode;
    @Schema(description = "是否已确认对公认证转账（StatusCode：1是 0否）")
    private StatusCodeEnum mkCompanyTransferStatusCode;
    @Schema(description = "入驻驳回原因")
    private String mkCompanyRejectReason;
    @Schema(description = "入驻审核人")
    private String mkCompanyAuditBy;
    @Schema(description = "入驻审核时间")
    private LocalDateTime mkCompanyAuditAt;

    @Schema(description = "是否测试数据（StatusCode：1是 0否）")
    private StatusCodeEnum mkCompanyTestStatusCode;

}
