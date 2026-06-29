package pub.module.trade.crud.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
 @Data
@TableName("td_goods_category")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="td_goods_category对象",description="td_goods_category对象")
public class TdGoodsCategory extends BaseEntity implements Serializable {


	@Schema(description = "编码")
    private java.lang.String tdGdCgyCode;
	@Schema(description = "名称")
    private java.lang.String tdGdCgyName;
}
