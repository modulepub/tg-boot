package pub.module.dating.api.service.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
 /**
  * 婚恋用户举报
  * @author tg
  * @since 2025-11-06
  * @version V1.0
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="dt_report对象",description="dt_report对象")
public class DtReportDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	/**主键*/
    @Schema(description = "主键")
    private java.lang.String id;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	/**编码*/
    @Schema(description = "编码")
    private java.lang.String dtRpCode;
	/**名称*/
    @Schema(description = "名称")
    private java.lang.String dtRpName;
	/**举报类型*/
    @Schema(description = "举报类型")
    private java.lang.String dtRpTypeCode;
	/**举报原因*/
    @Schema(description = "举报原因")
    private java.lang.String dtRpReason;
	/**举报人*/
    @Schema(description = "举报人")
    private java.lang.String dtSysUserCode;
	/**被举报人*/
    @Schema(description = "被举报人")
    private java.lang.String dtToSysUserCode;
}
