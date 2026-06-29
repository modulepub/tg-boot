package pub.module.verification.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 手机号姓名二要素核验记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "手机号二要素核验记录")
@TableName("vt_np_record")
public class NpRecord extends BaseEntity {

    @Schema(description = "业务流水号")
    private String npRecordCode;

    @Schema(description = "发起方业务模块编码")
    private String npRecordSourceModuleCode;

    @Schema(description = "发起方业务主体编码，如客户 cusCode")
    private String npRecordBizCode;

    @Schema(description = "手机号")
    private String npRecordPhone;

    @Schema(description = "姓名")
    private String npRecordRealName;

    @Schema(description = "是否通过：1通过 0未通过 E异常；阿里云 BizCode 见落库原始 JSON")
    private String npRecordPassedStatusCode;

    @Schema(description = "核验提供方编码")
    private String npRecordProviderCode;

    @Schema(description = "上游请求 ID")
    private String npRecordVendorRequestId;

    @Schema(description = "上游说明或错误信息")
    private String npRecordVendorMessage;

    @Schema(description = "基础运营商")
    private String npRecordBasicCarrier;

    @Schema(description = "上游响应原文摘要")
    private String npRecordVendorRaw;
}
