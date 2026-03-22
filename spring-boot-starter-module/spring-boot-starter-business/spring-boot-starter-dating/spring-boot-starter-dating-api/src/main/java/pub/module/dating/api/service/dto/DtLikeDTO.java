package pub.module.dating.api.service.dto;

import java.io.Serializable;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
  * 喜欢
  * @author tg
  * @since 2025-07-20
  * @version V1.0
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DtLikeDTO implements Serializable {
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
	/**编码*/
    //@Schema(description = "编码")
    private java.lang.String dtLikeCode;
	/**名称*/
    //@Schema(description = "名称")
    private java.lang.String dtLikeName;
	/**己方用户编码*/
    //@Schema(description = "己方用户编码")
    private java.lang.String dtLikeOwnSysUserCode;
	/**对方用户编码*/
    //@Schema(description = "对方用户编码")
    private java.lang.String dtLikeOtherSysUserCode;
	/**喜好程度*/
    //@Schema(description = "喜好程度")
    private java.lang.String dtLikeDegreeCode;
}
