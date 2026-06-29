package pub.module.dating.api.service.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
 @Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="dt_report对象",description="dt_report对象")
public class DtReportDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	@Schema(description = "主键")
    private java.lang.String id;
	@Schema(description = "创建人")
    private java.lang.String createBy;
		@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
@Schema(description = "创建日期")
    private java.util.Date createTime;
	@Schema(description = "更新人")
    private java.lang.String updateBy;
		@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
@Schema(description = "更新日期")
    private java.util.Date updateTime;
	@Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	@Schema(description = "编码")
    private java.lang.String dtRpCode;
	@Schema(description = "名称")
    private java.lang.String dtRpName;
	@Schema(description = "举报类型")
    private java.lang.String dtRpTypeCode;
	@Schema(description = "举报原因")
    private java.lang.String dtRpReason;
	@Schema(description = "举报人")
    private java.lang.String dtSysUserCode;
	@Schema(description = "被举报人")
    private java.lang.String dtToSysUserCode;
}
