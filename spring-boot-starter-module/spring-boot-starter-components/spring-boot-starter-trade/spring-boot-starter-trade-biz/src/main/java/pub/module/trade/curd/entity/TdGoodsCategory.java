package pub.module.trade.curd.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.data.api.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
 /**
  * td_goods_category
  * @author tg
  * @since 2025-12-09
  * @version V1.0
  */
@Data
@TableName("td_goods_category")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="td_goods_category对象",description="td_goods_category对象")
public class TdGoodsCategory extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


	/**编码*/
    @Schema(description = "编码")
    private java.lang.String tdGdCgyCode;
	/**名称*/
    @Schema(description = "名称")
    private java.lang.String tdGdCgyName;
}
