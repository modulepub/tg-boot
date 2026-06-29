package pub.module.verification.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.model.po.BaseEntity;
import pub.module.verification.api.constants.VtAssetCertProcessCodeEnum;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "资产认证记录")
@TableName("vt_asset_cert_record")
public class VtAssetCertRecord extends BaseEntity {

    @Schema(description = "资产认证记录编码")
    private String assetCertCode;

    @Schema(description = "客户编码")
    private String cusCode;

    @Schema(description = "客户昵称")
    private String cusNickName;

    @Schema(description = "提交红娘编码")
    private String submitMkCode;

    @Schema(description = "提交红娘姓名")
    private String submitMkName;

    @Schema(description = "行驶证照片")
    private String vehicleLicensePhoto;

    @Schema(description = "房产证照片")
    private String realEstateCertificatePhoto;

    @Schema(description = "婚姻状态证明照片")
    private String maritalStatusProofPhoto;

    @Schema(description = "诚实守信录制视频文件")
    private String honestyVideoFile;

    @Schema(description = "流程状态")
    private VtAssetCertProcessCodeEnum assetCertProcessCode;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "审核人用户编码")
    private String auditBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private LocalDateTime auditAt;
}
