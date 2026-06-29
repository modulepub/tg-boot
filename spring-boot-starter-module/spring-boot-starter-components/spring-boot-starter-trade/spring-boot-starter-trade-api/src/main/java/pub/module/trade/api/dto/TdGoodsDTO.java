package pub.module.trade.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 新增/维护商品入参
 *
 * @author tg
 * @since 2026-05-04
 */
@Data
@Schema(title = "商品入参 TdGoodsDTO")
public class TdGoodsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键 id")
    private String id;
    @Schema(description = "序号，列表排序用")
    private Long seqNo;
    @Schema(description = "编码，为空时由系统生成")
    private String tdGdCode;
    @Schema(description = "价格")
    private BigDecimal tdGdPrice;
    @Schema(description = "价值")
    private BigDecimal tdGdValue;
    @Schema(description = "描述")
    private String tdGdDescription;
    @Schema(description = "供应商")
    private String tdGdSysUserCode;
    @Schema(description = "供应商名称")
    private String tdGdSysUserRealName;
    @Schema(description = "供应商电话")
    private String tdGdSysUserPhone;
    @Schema(description = "启用状态")
    private String tdGdEnabledCode;
    @Schema(description = "服务期（天），null 或 0 表示无服务期")
    private Integer tdGdDayPeriod;
    @Schema(description = "名称")
    private String tdGdName;
    @Schema(description = "商品标签")
    private String tdGdTag;
    @Schema(description = "商品类目编码")
    private String tdGdCgyCode;
    @Schema(description = "商品类目名称")
    private String tdGdCgyName;
    @Schema(description = "库存数量")
    private BigDecimal tdGdInventoryNum;
    @Schema(description = "分佣比例")
    private BigDecimal tdGdCommissionRate;
    @Schema(description = "是否测试数据：1是 0否")
    private String tdGdTestStatusCode;
    @Schema(description = "是否隐藏：1是 0否（移动端不展示隐藏商品）")
    private String tdGdHiddenStatusCode;

    @Schema(description = "商品权益列表")
    private List<TdGoodsBenefitDTO> benefitList;
}
