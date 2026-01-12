package pub.module.dating.api.service.dto;

import java.io.Serializable;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
  * 相亲推荐
  * @author tg
  * @since 2025-06-15
  * @version V1.0
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DtRecommendedDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
    //@Schema(description = "主键")
    private java.lang.String id;
	/**创建人*/
    //@Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //@Schema(description = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    //@Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //@Schema(description = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    //@Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
	/**客户编码*/
    //@Schema(description = "客户编码")
    private java.lang.String dtRecommendedSysUserCode;
	/**被推荐的客户*/
    //@Schema(description = "被推荐的客户")
    private java.lang.String dtRecommendedToSysUserCode;
	/**推荐时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    //@Schema(description = "推荐时间")
    private java.util.Date dtRecommendedTime;
	/**意向ID*/
    //@Schema(description = "意向ID")
    private java.lang.String dtIntentionCode;
	/**意向名称*/
    //@Schema(description = "意向名称")
    private java.lang.String dtIntentionName;
}
