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
  * 消息模板
  * @author tg
  * @since 2025-10-27
  * @version V1.0
  */
@Data
@TableName("im_template")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="im_template对象",description="im_template对象")
public class ImTemplate implements Serializable {
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
	/**模板编码*/
    @Schema(description = "模板编码")
    private java.lang.String imTemplateCode;
	/**消息名称*/
    @Schema(description = "消息名称")
    private java.lang.String imTemplateName;
	/**消息类型*/
    @Schema(description = "消息类型")
    private java.lang.String imTemplateTypeCode;
	/**消息内容*/
    @Schema(description = "消息内容")
    private java.lang.String imTemplateContent;
}
