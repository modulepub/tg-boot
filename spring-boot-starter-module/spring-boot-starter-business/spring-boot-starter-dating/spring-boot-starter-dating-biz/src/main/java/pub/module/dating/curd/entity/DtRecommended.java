package pub.module.dating.curd.entity;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;

/**
 * 对象推荐 对象
 *
 * @author tg
 * 2026-03-30 00:52:26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "对象推荐")
public class DtRecommended extends BaseEntity {

    String recommendedCode;
    /**
     * 被推荐人
     */
    @Schema(description = "被推荐人")
    private String userCode;

    /**
     * 客户编号
     */
    private String cusCode;
    @Schema(description = "学历编码")
    private String cusEducationCode;

    @Schema(description = "学历名称")
    private String cusEducationName;
    /**
     * 客户生活照
     */
    @Schema(description = "客户生活照")
    private String cusLifePhoto;
    @Schema(description = "头像")
    private String cusAvatar;

    /**
     * 客户姓名
     */
    @Schema(description = "客户姓名")
    private String cusName;

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
     * 常驻城市名称
     */
    @Schema(description = "常驻城市名称")
    private String cusCityResidenceName;

    @Schema(description = "经纬度")
    private String cusResidenceLngLat;

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
     * 是否有意向
     */
    @Schema(description = "是否有意向")
    private String cusIntentionStatusCode;

    /**
     * 用户描述
     */
    @Schema(description = "用户描述")
    private String cusDesc;

    /**
     * 客户需求
     */
    @Schema(description = "客户需求")
    private String cusDemand;


    @Schema(description = "用户说说")
    private String cusMoment;

    /**
     * 最大年龄
     */
    @Schema(description = "最大年龄")
    private Integer intentionMaxAge;

    /**
     * 最小年龄
     */
    @Schema(description = "最小年龄")
    private Integer intentionMinAge;

    /**
     * 是否有房
     */
    @Schema(description = "是否有房")
    private String intentionHaveHouseCode;

    /**
     * 是否有车
     */
    @Schema(description = "是否有车")
    private String intentionHaveCarCode;




    /**
     * 城市
     */
    @Schema(description = "城市")
    private String intentionCityCode;


    /**
     * 性别
     */
    @Schema(description = "性别")
    private String intentionSexCode;

    /**
     * 是否接受残疾
     */
    @Schema(description = "是否接受残疾")
    private String intentionDisabledStatusCode;

    /** 推荐来源：free 免费推荐，pay 付费推荐，matchmaker 红娘推荐 */
    @Schema(description = "推荐来源编码：free/pay/matchmaker")
    private String recommendedSourceCode;

    /** 匹配分（0～100） */
    @Schema(description = "匹配分")
    private BigDecimal recommendedMatchScore;

}
