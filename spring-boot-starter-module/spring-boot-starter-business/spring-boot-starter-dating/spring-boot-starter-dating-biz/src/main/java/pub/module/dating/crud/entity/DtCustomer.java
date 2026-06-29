package pub.module.dating.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.api.constants.*;
import pub.module.system.api.constants.UserSexCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 客户 对象
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Data
@TableName("dt_customer")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户")
public class DtCustomer extends BaseEntity {
    /**
     * 客户编号
     */
    private String cusCode;

    @Schema(description = "客户生活照")
    private String cusLifePhoto;
    @Schema(description = "头像")
    private String cusAvatar;

    @Schema(description = "少年感照片")
    private String cusTeenagePhoto;

    @Schema(description = "客户姓名")
    private String cusName;

    @Schema(description = "客户昵称")
    private String cusNickName;

    @Schema(description = "证件号")
    private String cusIdNo;

    @Schema(description = "学历编码")
    private String cusEducationCode;

    @Schema(description = "学历名称")
    private String cusEducationName;
    @Schema(description = "证件类型")
    private CusIdTypeCodeEnum cusIdTypeCode;

    @Schema(description = "身份认证状态")
    private StatusCodeEnum cusIdentityAuthenticatedStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实名认证时间")
    private LocalDateTime cusIdentityAuthenticatedTime;

    @Schema(description = "客户性别")
    private UserSexCodeEnum cusSexCode;

    @Schema(description = "微信号")
    private String cusWxIdNo;

    @Schema(description = "是否点亮爱与诚")
    private StatusCodeEnum cusLsStatusCode;

    @Schema(description = "是否测试数据（StatusCode：1是 0否）")
    private StatusCodeEnum cusTestStatusCode;

    @Schema(description = "客户身份编码：self 本人，parent（界面展示家长）")
    private CusKinshipCodeEnum cusKinshipCode;

    @Schema(description = "年龄")
    private Long cusAge;

    @Schema(description = "年龄")
    private Date cusBirthday;

    @Schema(description = "身高(cm)")
    private Long cusHeight;

    @Schema(description = "体重")
    private Long cusWeight;

    @Schema(description = "婚姻状况")
    private StatusCodeEnum cusMaritalStatusCode;

    @Schema(description = "婚姻状态认证：1 已认证 0 未认证")
    private StatusCodeEnum cusMaritalStatusAuthenticatedStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "婚姻状态认证时间")
    private LocalDateTime cusMaritalStatusAuthenticatedTime;

    @Schema(description = "是否二婚")
    private StatusCodeEnum cusRemarriageStatusCode;

    @Schema(description = "是否残疾")
    private StatusCodeEnum cusDisabledStatusCode;


    @Schema(description = "经纬度")
    private String cusResidenceLngLat;

    @Schema(description = "生活城市")
    private String cusCityResidenceCode;

    @Schema(description = "生活城市名称")
    private String cusCityResidenceName;

    @Schema(description = "是否有车")
    private StatusCodeEnum cusHaveCarStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "车产认证时间")
    private LocalDateTime cusHaveCarAuthenticatedTime;

    @Schema(description = "行驶证")
    private String cusVehicleLicensePhoto;

    @Schema(description = "是否有房")
    private StatusCodeEnum cusHaveHouseStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "房产认证时间")
    private LocalDateTime cusHaveHouseAuthenticatedTime;

    @Schema(description = "房产证")
    private String cusRealEstateCertificatePhoto;

    @Schema(description = "车产信息认证：1 已认证")
    private StatusCodeEnum cusCarAssetCertStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "车产信息认证时间")
    private LocalDateTime cusCarAssetCertTime;

    @Schema(description = "房产证认证：1 已认证")
    private StatusCodeEnum cusHouseAssetCertStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "房产证认证时间")
    private LocalDateTime cusHouseAssetCertTime;

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

    @Schema(description = "客户等级0-8：无效、普通、价值、黄金、钻石、至尊")
    private CusLevelCodeEnum cusLevelCode;




    @Schema(description = "是否有意向")
    private StatusCodeEnum cusIntentionStatusCode;

    @Schema(description = "意向等级")
    private CusIntentionLevelCodeEnum cusIntentionLevelCode;

    @Schema(description = "用户描述")
    private String cusDesc;

    @Schema(description = "用户备注")
    private String cusRemark;

    @Schema(description = "客户需求")
    private String cusDemand;

    @Schema(description = "用户说说")
    private String cusMoment;

    @Schema(description = "资料是否已完善：1 已完善，0 未完善")
    private StatusCodeEnum cusComleteProfileStatusCode;

    @Schema(description = "审核流程：1待修改 2审核中 3审核通过")
    private CusAuditProcessCodeEnum cusAuditProcessCode;

    @Schema(description = "是否隐藏：1 隐藏，0 不隐藏")
    private StatusCodeEnum cusHiddenStatusCode;

    @Schema(description = "是否入库")
    private StatusCodeEnum cusPoolStatusCode;
    @Schema(description = "是否分配营销人员")
    private StatusCodeEnum cusAssignSalesStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "分配时间")
    private LocalDateTime cusAssignSalesTime;

    @TableField(exist = false)
    @Schema(description = "分配时间检索")
    private String[] cusAssignSalesTimeRangeArray;
    @Schema(description = "是否分配服务人员")
    private StatusCodeEnum cusAssignServersStatusCode;

    @Schema(description = "微信号")
    private String cusWechatId;
    @Schema(description = "客户用户号")
    @TableField("cus_user_code")
    private String cusUserCode;

    @TableField(exist = false)
    @Schema(description = "推荐人用户编码")
    private String cusReferrerUserCode;

    @TableField(exist = false)
    @Schema(description = "推荐人用户姓名")
    private String cusReferrerUserName;

    @Schema(description = "是否成交")
    private StatusCodeEnum cusDealtStatusCode;
    @Schema(description = "是否完单")
    private StatusCodeEnum cusDealtCompleteStatusCode;

    @Schema(description = "是否跟进")
    private StatusCodeEnum cusFollowUpStatusCode;
    @Schema(description = "跟进提醒类型")
    private CusFollowUpReminderTypeCodeEnum cusFollowUpReminderTypeCode;
    @Schema(description = "会员类型编码")
    private String cusMemberTypeCode;
    @Schema(description = "会员类型名称")
    private String cusMemberTypeName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "会员到期日")
    private LocalDateTime cusMemberExpireTime;
    @Schema(description = "牵手次数")
    private Long cusHandholdsNum;
    @Schema(description = "添加好友次数权益（历史累计字段，新逻辑请使用 cusAddFriendDayLimit）")
    private Long cusAddFriendRightValue;
    @Schema(description = "推荐次数权益（历史累计字段，新逻辑请使用 cusRecommendDayLimit）")
    private Long cusRecommendRightValue;
    @Schema(description = "牵线次数权益（历史累计字段，新逻辑请使用 cusMatchDayLimit）")
    private Long cusMatchRightValue;
    @Schema(description = "每日添加好友次数上限")
    private Long cusAddFriendDayLimit;
    @Schema(description = "每日推荐次数上限")
    private Long cusRecommendDayLimit;
    @Schema(description = "每日牵线次数上限")
    private Long cusMatchDayLimit;
}
