package pub.module.trade.curd.entity;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * 商品实体类
 * 对应数据库表td_goods，存储商品信息
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
@TableName("td_goods")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title="td_goods对象",description="td_goods对象")
public class TdGoods extends BaseEntity implements Serializable {

	/**编码*/
    @Schema(description = "编码")
    private java.lang.String tdGdCode;
	/**价格*/
    @Schema(description = "价格")
    private java.math.BigDecimal tdGdPrice;
	/**价值*/
    @Schema(description = "价值")
    private java.math.BigDecimal tdGdValue;
	/**描述*/
    @Schema(description = "描述")
    private java.lang.String tdGdDescription;
	/**供应商*/
    @Schema(description = "供应商")
    private java.lang.String tdGdSysUserCode;
	/**供应商名称*/
    @Schema(description = "供应商名称")
    private java.lang.String tdGdSysUserRealName;
	/**供应商电话*/
    @Schema(description = "供应商电话")
    private java.lang.String tdGdSysUserPhone;
	/**启用状态*/
    @Schema(description = "启用状态")
    private java.lang.String tdGdEnabledCode;
	/**服务期限*/
    @Schema(description = "服务期限")
    private java.lang.String tdGdPeriod;
	/**名称*/
    @Schema(description = "名称")
    private java.lang.String tdGdName;
	/**商品标签*/
    @Schema(description = "商品标签")
    private java.lang.String tdGdTag;
	/**商品类目*/
    @Schema(description = "商品类目编码")
    private java.lang.String tdGdCgyCode;
    /**名称*/
    @Schema(description = "商品类目名称")
    private java.lang.String tdGdCgyName;
	/**库存数量*/
    @Schema(description = "库存数量")
    private java.math.BigDecimal tdGdInventoryNum;
}
