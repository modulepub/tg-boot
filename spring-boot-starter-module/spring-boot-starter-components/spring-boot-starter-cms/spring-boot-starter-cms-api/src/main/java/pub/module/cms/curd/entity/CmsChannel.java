package pub.module.cms.curd.entity;

import java.io.Serial;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
 /**
  * CMS栏目
  * @author tg
  * @since 2025-09-29
  * @version V1.0
  */
@Data
@TableName("cms_channel")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="cms_channel对象")
public class CmsChannel implements Serializable {
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
	/**栏目编码*/
    @Schema(description = "栏目编码")
    private java.lang.String ccCode;
	/**父级栏目编码*/
    @Schema(description = "父级栏目编码")
    private java.lang.String ccParentCode;
	/**父级栏目名称*/
    @Schema(description = "父级栏目名称")
    private java.lang.String ccParentName;
	/**栏目名称*/
    @Schema(description = "栏目名称")
    private java.lang.String ccName;
	/**菜单链接*/
    @Schema(description = "菜单链接")
    private java.lang.String ccUrl;
	/**图片*/
    @Schema(description = "图片")
    private java.lang.String ccHeadImg;
	/**摘要*/
    @Schema(description = "摘要")
    private java.lang.String ccSummary;
	/**发布时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发布时间")
    private java.util.Date ccPublishTime;
	/**发布状态(1已发布，0未发布)*/
    @Schema(description = "发布状态(1已发布，0未发布)")
    private java.lang.String ccPublishStatusCode;
     @Schema(description = "子栏目")
    @TableField(exist = false)
    List<CmsChannel> children;
}
