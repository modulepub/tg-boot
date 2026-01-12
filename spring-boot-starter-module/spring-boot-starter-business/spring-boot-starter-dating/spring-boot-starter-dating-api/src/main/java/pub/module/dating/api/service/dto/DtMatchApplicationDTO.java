package pub.module.dating.api.service.dto;

import java.io.Serializable;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
  * 牵线申请
  * @author tg
  * @since 2025-07-21
  * @version V1.0
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DtMatchApplicationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
    //@Schema(description = "id")
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
    private java.lang.String dtMaCode;
	/**名称*/
    //@Schema(description = "名称")
    private java.lang.String dtMaName;
	/**红娘用户编码*/
    //@Schema(description = "红娘用户编码")
    private java.lang.String dtMaMatchmakerSysUserCode;
	/**追求者*/
    //@Schema(description = "追求者")
    private java.lang.String dtMaPursuingSysUserCode;
	/**被追求者*/
    //@Schema(description = "被追求者")
    private java.lang.String dtMaPursuedSysUserCode;
	/**服务状态*/
    //@Schema(description = "服务状态")
    private java.lang.String dtMaServiceStatusCode;
}
