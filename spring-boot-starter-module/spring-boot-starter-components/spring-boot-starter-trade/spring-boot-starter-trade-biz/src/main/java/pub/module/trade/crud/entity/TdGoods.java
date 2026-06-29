package pub.module.trade.crud.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
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

	@Schema(description = "编码")
    private java.lang.String tdGdCode;
	@Schema(description = "价格")
    private java.math.BigDecimal tdGdPrice;
	@Schema(description = "价值")
    private java.math.BigDecimal tdGdValue;
	@Schema(description = "描述")
    private java.lang.String tdGdDescription;
	@Schema(description = "供应商")
    private java.lang.String tdGdSysUserCode;
	@Schema(description = "供应商名称")
    private java.lang.String tdGdSysUserRealName;
	@Schema(description = "供应商电话")
    private java.lang.String tdGdSysUserPhone;
	@Schema(description = "启用状态")
    private java.lang.String tdGdEnabledCode;
	@Schema(description = "服务期（天），null 或 0 表示无服务期")
    private Integer tdGdDayPeriod;
	@Schema(description = "名称")
    private java.lang.String tdGdName;
	@Schema(description = "商品标签")
    private java.lang.String tdGdTag;
	@Schema(description = "商品类目编码")
    private java.lang.String tdGdCgyCode;
    @Schema(description = "商品类目名称")
    private java.lang.String tdGdCgyName;
	@Schema(description = "库存数量")
    private java.math.BigDecimal tdGdInventoryNum;
	@Schema(description = "分佣比例")
    private java.math.BigDecimal tdGdCommissionRate;
	@Schema(description = "是否测试数据：1是 0否")
    private java.lang.String tdGdTestStatusCode;
	@Schema(description = "是否隐藏：1是 0否（移动端不展示隐藏商品）")
    private java.lang.String tdGdHiddenStatusCode;

    @TableField(exist = false)
    @Schema(description = "商品权益列表")
    private List<TdGoodsBenefit> benefitList;
}
