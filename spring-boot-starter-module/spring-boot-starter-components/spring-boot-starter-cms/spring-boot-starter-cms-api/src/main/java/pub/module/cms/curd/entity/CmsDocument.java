package pub.module.cms.curd.entity;

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
  * CMS文档
  * @author tg
  * @since 2025-09-29
  * @version V1.0
  */
@Data
@TableName("cms_document")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="cms_document对象")
public class CmsDocument implements Serializable {
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
	/**栏目名称*/
    @Schema(description = "栏目名称")
    private java.lang.String ccName;
	/**图片*/
    @Schema(description = "图片")
    private java.lang.String cdHeadImg;
	/**编码*/
    @Schema(description = "编码")
    private java.lang.String cdCode;
	/**名称*/
    @Schema(description = "名称")
    private java.lang.String cdName;
	/**摘要*/
    @Schema(description = "摘要")
    private java.lang.String cdSummary;
	/**内容*/
    @Schema(description = "内容")
    private java.lang.String cdContent;
	/**发布时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发布时间")
    private java.util.Date cdPublishTime;
	/**发布状态(1已发布，0未发布)*/
    @Schema(description = "发布状态(1已发布，0未发布)")
    private java.lang.String cdPublishStatusCode;
}
