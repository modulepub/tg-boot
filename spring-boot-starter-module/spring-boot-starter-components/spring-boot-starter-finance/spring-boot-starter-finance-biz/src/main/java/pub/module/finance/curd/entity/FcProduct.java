package pub.module.finance.curd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.data.api.entity.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品管理
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-11
 */
@Data
@TableName("fc_product")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "fc_product对象")
public class FcProduct extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * LOGO
     */
    @Schema(description = "LOGO")
    private String fcProductLogo;

    @Schema(description = "产品编码")
    private String fcProductCode;
    /**
     * 产品来源
     */
    @Schema(description = "产品来源")
    private String fcProductSourceCode;
    /**
     * 产品形式（全流程/半流程）
     */
    @Schema(description = "产品形式（全流程/半流程）")
    private String fcProductFormCode;
    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String fcProductName;
    /**
     * 商户编码
     */
    @Schema(description = "商户编码")
    private String fcMerchantCode;
    /**
     * 下款率
     */
    @Schema(description = "下款率")
    private String fcProductDisburseRate;
    /**
     * 产品类型
     */
    @Schema(description = "产品类型")
    private String fcProductTypeCode;
    /**
     * 产品描述
     */
    @Schema(description = "产品描述")
    private String fcProductDescription;
    /**
     * 标签
     */
    @Schema(description = "标签")
    private String fcProductLabel;
    /**
     * 申请条件
     */
    @Schema(description = "申请条件")
    private String fcProductApRequirement;
    /**
     * 是否上架
     */
    @Schema(description = "是否上架")
    private Integer fcProductShelfStatusCode;
    /**
     * 是否开启风控
     */
    @Schema(description = "是否开启风控")
    private Integer fcProductRiskCtlCode;
    /**
     * 最小额度
     */
    @Schema(description = "最小额度")
    private BigDecimal fcProductMinAmount;
    /**
     * 最大额度
     */
    @Schema(description = "最大额度")
    private BigDecimal fcProductMaxAmount;
    /**
     * 最小期限
     */
    @Schema(description = "最小期限")
    private Integer fcProductMinPeriod;
    /**
     * 最大期限
     */
    @Schema(description = "最大期限")
    private Integer fcProductMaxPeriod;

    @Schema(description = "年利率")
    private BigDecimal fcProductYearInterestRate;
    /**
     * 平均下款额度
     */
    @Schema(description = "平均下款额度")
    private BigDecimal fcProductDisburseAvAmt;
    /**
     * 协议配置
     */
    @Schema(description = "协议配置")
    private String fcProductProtocolConfig;
    /**
     * H5注册链接
     */
    @Schema(description = "H5注册链接")
    private String fcProductRegLink;
    /**
     * 是否开启撞库  0否 1 是
     */
    @Schema(description = "是否开启撞库  0否 1 是")
    private Integer fcProductZkFlagCode;
    /**
     * 撞库URL
     */
    @Schema(description = "撞库URL")
    private String fcProductZkUrl;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer fcProductSortOrder;
    /**
     * 价格
     */
    @Schema(description = "价格")
    private BigDecimal fcProductPrice;
    /**
     * 城市限定类型
     */
    @Schema(description = "城市限定类型")
    private String fcProductCityRtnCod;
    /**
     * 城市限定
     */
    @Schema(description = "城市限定")
    private String fcProductCityRestriction;
    /**
     * 城市限定code
     */
    @Schema(description = "城市限定code")
    private String fcProductCityRtnCode;
    /**
     * 是否开启定量
     */
    @Schema(description = "是否开启定量")
    private Integer fcProductQtnCode;
    /**
     * 定量阈值
     */
    @Schema(description = "定量阈值")
    private Integer fcProductThdValue;
    /**
     * 今日点击数
     */
    @Schema(description = "今日点击数")
    private Integer fcProductTodayNumber;
    /**
     * 执行时段
     */
    @Schema(description = "执行时段")
    private String fcProductExecutionPeriod;
    /**
     * 当前时段能否匹配
     */
    @Schema(description = "当前时段能否匹配")
    private Integer fcProductCurrentMatch;
    /**
     * 渠道限定类型
     */
    @Schema(description = "渠道限定类型")
    private String fcProductCnlRtnCode;
    /**
     * 渠道限定
     */
    @Schema(description = "渠道限定")
    private String fcProductChannelRtn;
    /**
     * 展示位置 0贷超 1拒量 2消息板块
     */
    @Schema(description = "展示位置 0贷超 1拒量 2消息板块")
    private String fcProductDisplayAddr;
    /**
     * 展示设备
     */
    @Schema(description = "展示设备")
    private String fcProductDisplayEqt;
    /**
     * 是否在H5展示
     */
    @Schema(description = "是否在H5展示")
    private Integer fcProductShowAtH5;
    /**
     * 准入域名
     */
    @Schema(description = "准入域名")
    private String fcProductAcsDomainName;
    /**
     * 最小年龄
     */
    @Schema(description = "最小年龄")
    private Integer fcProductMinAge;
    /**
     * 最大年龄
     */
    @Schema(description = "最大年龄")
    private Integer fcProductMaxAge;
    /**
     * 户籍地限制类型
     */
    @Schema(description = "户籍地限制类型")
    private String fcProductDmlRtnCode;
    /**
     * 户籍地限制城市
     */
    @Schema(description = "户籍地限制城市")
    private String fcProductDomicileRtn;
    /**
     * 户籍地限制城市code
     */
    @Schema(description = "户籍地限制城市code")
    private String fcProductDmlLimRtnCode;
    /**
     * 禁入手机号段（前3位）
     */
    @Schema(description = "禁入手机号段（前3位）")
    private String fcProductDisabledPhone;
    /**
     * 空值是否通过
     */
    @Schema(description = "空值是否通过")
    private Integer fcProductPassNull;
    /**
     * 是否跳转外链（1-是，0-否）
     */
    @Schema(description = "是否跳转外链（1-是，0-否）")
    private Integer fcProductIsOutsideChain;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String fcProductMsg;
    /**
     * 可见风控类型
     */
    @Schema(description = "可见风控类型")
    private String fcProductFkCode;
    /**
     * 年龄限制（1-开启，0-关闭）
     */
    @Schema(description = "年龄限制（1-开启，0-关闭）")
    private Integer fcProductAgeLimit;
    @Schema(description = "借款合同")
    private String fcProductLoanContract;
    @Schema(description = "担保合同")
    private String fcProductGuaranteeContract;
    @Schema(description = "服务协议")
    private String fcProductServiceContract;
    @Schema(description = "最大年利率")
    private BigDecimal fcProductYearInterestMaxRate;
    @Schema(description = "最小年利率")
    private BigDecimal fcProductYearInterestMinRate;
    @Schema(description = "配置参数")
    private String fcProductConfigJson;
}
