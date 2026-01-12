package pub.module.contract.curd.entity;

import java.io.Serial;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.data.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
 /**
  * ct_template
  * @author tg
  * @since 2025-12-09
  * @version V1.0
  */
@Data
@TableName("ct_template")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="ct_template对象",description="ct_template对象")
public class CtTemplate extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


	/**合同模板编码*/
    @Schema(description = "合同模板编码")
    private java.lang.String ctTemplateCode;
	/**合同模板名称*/
    @Schema(description = "合同模板名称")
    private java.lang.String ctTemplateName;
	/**合同模板文件*/
    @Schema(description = "合同模板文件")
    private java.lang.String ctTemplateFile;
	/**合同模板分类*/
    @Schema(description = "合同模板分类")
    private java.lang.String ctClassificationCode;
}
