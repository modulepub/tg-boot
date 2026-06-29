package pub.module.trade.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商品权益实体
 */
@Data
@TableName("td_goods_benefit")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "td_goods_benefit", description = "商品权益")
public class TdGoodsBenefit extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品权益编码")
    private String tdGdBnfCode;

    @Schema(description = "商品编码")
    private String tdGdCode;

    @Schema(description = "权益key")
    private String tdGdBnfKey;

    @Schema(description = "权益值")
    private Long tdGdBnfValue;

    @Schema(description = "权益描述")
    private String tdGdBnfDesc;
}
