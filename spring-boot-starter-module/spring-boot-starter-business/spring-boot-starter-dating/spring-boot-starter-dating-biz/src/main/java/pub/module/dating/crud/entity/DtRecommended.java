package pub.module.dating.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.api.constants.*;
import pub.module.dating.api.constants.RecommendedSourceCodeEnum;
import pub.module.system.api.constants.UserSexCodeEnum;

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
    @Schema(description = "客户生活照")
    private String cusLifePhoto;
    @Schema(description = "头像")
    private String cusAvatar;

    @Schema(description = "客户姓名")
    private String cusName;

    @Schema(description = "客户昵称")
    private String cusNickName;

    @Schema(description = "实名认证状态")
    private StatusCodeEnum cusIdentityAuthenticatedStatusCode;

    @Schema(description = "是否点亮爱与诚（冗余自客户表 cus_ls_status_code，1 点亮）")
    private StatusCodeEnum cusLsStatusCode;

    @Schema(description = "是否隐藏（冗余自客户表 cus_hidden_status_code，1 隐藏 0 不隐藏）")
    private StatusCodeEnum cusHiddenStatusCode;

    @Schema(description = "客户性别")
    private UserSexCodeEnum cusSexCode;

    @Schema(description = "年龄")
    private Long cusAge;

    @Schema(description = "身高(cm)")
    private Long cusHeight;

    @Schema(description = "体重")
    private Long cusWeight;

    @Schema(description = "婚姻状况")
    private StatusCodeEnum cusMaritalStatusCode;

    @Schema(description = "牵手次数")
    private Long cusHandholdsNum;

    @Schema(description = "生活城市")
    private String cusCityResidenceCode;

    @Schema(description = "生活城市名称")
    private String cusCityResidenceName;

    @Schema(description = "经纬度")
    private String cusResidenceLngLat;

    @Schema(description = "是否有车")
    private StatusCodeEnum cusHaveCarStatusCode;

    @Schema(description = "行驶证")
    private String cusVehicleLicensePhoto;

    @Schema(description = "是否有房")
    private StatusCodeEnum cusHaveHouseStatusCode;

    @Schema(description = "房产证")
    private String cusRealEstateCertificatePhoto;

    @Schema(description = "职业描述")
    private String cusOccupationalDescription;

    @Schema(description = "年收入")
    private BigDecimal cusAnnualIncomeAmount;

    @Schema(description = "年收入证明图片")
    private String cusAnnualIncomeAuthenticatedPhoto;

    @Schema(description = "手机号")
    private String cusPhone;

    @Schema(description = "来源")
    private CusSourceCodeEnum cusSourceCode;

    @Schema(description = "用户标签")
    private CusTagCodeEnum cusTagCode;

    @Schema(description = "是否有意向")
    private StatusCodeEnum cusIntentionStatusCode;

    @Schema(description = "用户描述")
    private String cusDesc;

    @Schema(description = "客户需求")
    private String cusDemand;


    @Schema(description = "用户说说")
    private String cusMoment;

    @Schema(description = "最大年龄")
    private Integer intentionMaxAge;

    @Schema(description = "最小年龄")
    private Integer intentionMinAge;

    @Schema(description = "是否有房")
    private StatusCodeEnum intentionHaveHouseCode;

    @Schema(description = "是否有车")
    private StatusCodeEnum intentionHaveCarCode;




    @Schema(description = "城市")
    private String intentionCityCode;


    @Schema(description = "性别")
    private UserSexCodeEnum intentionSexCode;

    @Schema(description = "是否接受残疾")
    private StatusCodeEnum intentionDisabledStatusCode;

    @Schema(description = "对方择偶最小年龄（列表联查 dt_intention，非表字段）")
    @TableField(exist = false)
    private Integer recommendedTargetIntentionMinAge;

    @Schema(description = "对方择偶最大年龄（列表联查 dt_intention，非表字段）")
    @TableField(exist = false)
    private Integer recommendedTargetIntentionMaxAge;

    @Schema(description = "对方择偶房产要求（列表联查 dt_intention，非表字段）")
    @TableField(exist = false)
    private StatusCodeEnum recommendedTargetIntentionHaveHouseCode;

    @Schema(description = "对方择偶车辆要求（列表联查 dt_intention，非表字段）")
    @TableField(exist = false)
    private StatusCodeEnum recommendedTargetIntentionHaveCarCode;

    @Schema(description = "对方择偶城市编码（列表联查 dt_intention，非表字段）")
    @TableField(exist = false)
    private String recommendedTargetIntentionCityCode;

    @Schema(description = "对方择偶残疾优先（列表联查 dt_intention，非表字段）")
    @TableField(exist = false)
    private StatusCodeEnum recommendedTargetIntentionDisabledStatusCode;

    @Schema(description = "推荐来源编码：free/pay/matchmaker")
    private RecommendedSourceCodeEnum recommendedSourceCode;

    @Schema(description = "匹配分")
    private BigDecimal recommendedMatchScore;

    @Schema(description = "客户删除状态：1 用户已从推荐列表删除 0 可见")
    private StatusCodeEnum recommendedCusDelStatusCode;

}
