package pub.module.im.curd.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
 /**
  * 客服坐席
  * @author tg
  * @since 2025-10-03
  * @version V1.0
  */
@Data
@TableName("im_csr")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="im_csr对象")
public class ImCsr implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
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
	/**用户编码*/
    @Schema(description = "用户编码")
    private java.lang.String userCode;
	/**客服工号*/
    @Schema(description = "客服工号")
    private java.lang.String imCsrCode;
	/**客服称呼*/
    @Schema(description = "客服称呼")
    private java.lang.String imCsrName;
	/**现接待人数*/
    @Schema(description = "现接待人数")
    private java.lang.Integer imCsrCurrentSrNum;
	/**总接待人数*/
    @Schema(description = "总接待人数")
    private java.lang.Integer imCsrSrNum;
	/**是否暂停服务*/
    @Schema(description = "是否暂停服务")
    private java.lang.String imCsrServiceSuspendedCode;
}
