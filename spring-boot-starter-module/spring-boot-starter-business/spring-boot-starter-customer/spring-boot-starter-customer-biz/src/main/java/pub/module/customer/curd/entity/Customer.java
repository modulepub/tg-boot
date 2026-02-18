package pub.module.customer.curd.entity;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.api.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 客户 对象
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户")
public class Customer extends BaseEntity {
    /**
     * 客户编号
     */
    private String cusCode;

    /**
     * 客户生活照
     */
    @Schema(description = "客户生活照")
    private String cusLifePhoto;

    /**
     * 客户姓名
     */
    @Schema(description = "客户姓名")
    private String cusName;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    private String cusIdCardNum;

    /**
     * 身份认证状态
     */
    @Schema(description = "身份认证状态")
    private String cusIdentityAuthenticatedStatusCode;

    /**
     * 客户性别
     */
    @Schema(description = "客户性别")
    private String cusSexCode;

    /**
     * 年龄
     */
    @Schema(description = "年龄")
    private Long cusAge;

    /**
     * 身高(cm)
     */
    @Schema(description = "身高(cm)")
    private Long cusHeight;

    /**
     * 体重（kg)
     */
    @Schema(description = "体重")
    private Long cusWeight;

    /**
     * 婚姻状况
     */
    @Schema(description = "婚姻状况")
    private String cusMaritalStatusCode;

    /**
     * 牵手次数
     */
    @Schema(description = "牵手次数")
    private Long cusHandholdsNum;

    /**
     * 常驻城市
     */
    @Schema(description = "常驻城市")
    private String cusCityResidenceCode;

    /**
     * 是否有车
     */
    @Schema(description = "是否有车")
    private String cusHaveCarStatusCode;

    /**
     * 行驶证
     */
    @Schema(description = "行驶证")
    private String cusVehicleLicensePhoto;

    /**
     * 是否有房
     */
    @Schema(description = "是否有房")
    private String cusHaveHouseStatusCode;

    /**
     * 房产证
     */
    @Schema(description = "房产证")
    private String cusRealEstateCertificatePhoto;

    /**
     * 职业描述
     */
    @Schema(description = "职业描述")
    private String cusOccupationalDescription;

    /**
     * 年收入
     */
    @Schema(description = "年收入")
    private BigDecimal cusAnnualIncomeAmount;

    /**
     * 年收入证明图片
     */
    @Schema(description = "年收入证明图片")
    private String cusAnnualIncomeAuthenticatedPhoto;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String cusPhone;

    /**
     * 来源
     */
    @Schema(description = "来源")
    private String cusSourceCode;

    /**
     * 用户标签
     */
    @Schema(description = "用户标签")
    private String cusTagCode;

    /**
     * 客户等级0-8：无效、普通、价值、黄金、钻石、至尊
     */
    @Schema(description = "客户等级0-8：无效、普通、价值、黄金、钻石、至尊")
    private String cusLevelCode;

    @Schema(description = "是否有意向")
    private String cusIntentionStatusCode;

    @Schema(description = "意向等级")
    private String cusIntentionLevelCode;

    /**
     * 用户描述
     */
    @Schema(description = "用户描述")
    private String cusDesc;

    /**
     * 用户描述
     */
    @Schema(description = "用户备注")
    private String cusRemark;

    /**
     * 客户需求
     */
    @Schema(description = "客户需求")
    private String cusDemand;

    /**
     * 是否入库
     */
    @Schema(description = "是否入库")
    private String cusPoolStatusCode;
    @Schema(description = "是否分配营销人员")
    private String cusAssignSalesStatusCode;
    @Schema(description = "是否分配服务人员")
    private String cusAssignServersStatusCode;

    /**
     * 微信号
     */
    @Schema(description = "微信号")
    private String cusWechatId;
    /**
     * 客户用户号
     */
    @Schema(description = "客户用户号")
    private String cusUserCode;

    /**
     * 是否成交
     */
    @Schema(description = "是否成交")
    private String cusDealtStatusCode;
    /**
     * 是否完单
     */
    @Schema(description = "是否完单")
    private String cusDealtCompleteStatusCode;

    /**
     * 是否跟进
     */
    @Schema(description = "是否跟进")
    private String cusFollowUpStatusCode;

}
