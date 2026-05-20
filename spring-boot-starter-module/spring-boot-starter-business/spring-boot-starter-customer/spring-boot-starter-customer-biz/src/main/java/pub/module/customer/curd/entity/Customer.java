package pub.module.customer.curd.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.model.po.BaseEntity;
import pub.module.customer.api.constants.*;
import pub.module.system.api.constants.UserSexCodeEnum;
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
    @Schema(description = "头像")
    private String cusAvatar;

    /**
     * 少年感照片
     */
    @Schema(description = "少年感照片")
    private String cusTeenagePhoto;

    /**
     * 客户姓名
     */
    @Schema(description = "客户姓名")
    private String cusName;

    @Schema(description = "客户昵称")
    private String cusNickName;

    /**
     * 身份证号
     */
    @Schema(description = "证件号")
    private String cusIdNo;

    @Schema(description = "学历编码")
    private String cusEducationCode;

    @Schema(description = "学历名称")
    private String cusEducationName;
    /**
     * 证件类型
     */
    @Schema(description = "证件类型")
    private CusIdTypeCodeEnum cusIdTypeCode;

    /**
     * 身份认证状态
     */
    @Schema(description = "身份认证状态")
    private String cusIdentityAuthenticatedStatusCode;

    /**
     * 客户性别
     */
    @Schema(description = "客户性别")
    private UserSexCodeEnum cusSexCode;

    /**
     * 微信号
     */
    @Schema(description = "微信号")
    private String cusWxIdNo;

    @Schema(description = "是否点亮爱与诚")
    private String cusLsStatusCode;

    /**
     * 客户身份（字典：self 本人；parent 客户端展示为「家长」）
     */
    @Schema(description = "客户身份编码：self 本人，parent（界面展示家长）")
    private String cusKinshipCode;

    /**
     * 年龄
     */
    @Schema(description = "年龄")
    private Long cusAge;

    /**
     * 生日
     */
    @Schema(description = "年龄")
    private Date cusBirthday;

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
     * 是否二婚（字典：1 是 / 0 否，与前端约定一致即可）
     */
    @Schema(description = "是否二婚")
    private String cusRemarriageStatusCode;

    /**
     * 是否残疾
     */
    @Schema(description = "是否残疾")
    private String cusDisabledStatusCode;

    /**
     * 牵手次数
     */
    @Schema(description = "牵手次数")
    private Long cusHandholdsNum;

    /**
     * 添加好友次数权益
     */
    @Schema(description = "添加好友次数权益")
    private Long cusAddFriendRightValue;

    /**
     * 推荐次数权益
     */
    @Schema(description = "推荐次数权益")
    private Long cusRecommendRightValue;

    /**
     * 牵线次数权益
     */
    @Schema(description = "牵线次数权益")
    private Long cusMatchRightValue;

    @Schema(description = "经纬度")
    private String cusResidenceLngLat;

    /**
     * 生活城市
     */
    @Schema(description = "生活城市")
    private String cusCityResidenceCode;

    /**
     * 生活城市名称
     */
    @Schema(description = "生活城市名称")
    private String cusCityResidenceName;

    /**
     * 是否有车
     */
    @Schema(description = "是否有车")
    private CusHaveCarStatusCodeEnum cusHaveCarStatusCode;

    /**
     * 行驶证
     */
    @Schema(description = "行驶证")
    private String cusVehicleLicensePhoto;

    /**
     * 是否有房
     */
    @Schema(description = "是否有房")
    private CusHaveHouseStatusCodeEnum cusHaveHouseStatusCode;

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
    private CusSourceCodeEnum cusSourceCode;

    /**
     * 用户标签
     */
    @Schema(description = "用户标签")
    private CusTagCodeEnum cusTagCode;

    /**
     * 客户等级0-8：无效、普通、价值、黄金、钻石、至尊
     */
    @Schema(description = "客户等级0-8：无效、普通、价值、黄金、钻石、至尊")
    private CusLevelCodeEnum cusLevelCode;

    @Schema(description = "是否有意向")
    private CusIntentionStatusCodeEnum cusIntentionStatusCode;

    @Schema(description = "意向等级")
    private CusIntentionLevelCodeEnum cusIntentionLevelCode;

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

    @Schema(description = "用户说说")
    private String cusMoment;

    /**
     * 主页/基本资料是否已完善（1 已完善 0 未完善；持久化列 cus_comlete_profile_status_code）
     */
    @Schema(description = "资料是否已完善：1 已完善，0 未完善")
    private String cusComleteProfileStatusCode;

    /**
     * 是否入库
     */
    @Schema(description = "是否入库")
    private CusPoolStatusCodeEnum cusPoolStatusCode;
    @Schema(description = "是否分配营销人员")
    private CusAssignSalesStatusCodeEnum cusAssignSalesStatusCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "分配时间")
    private LocalDateTime cusAssignSalesTime;

    @TableField(exist = false)
    @Schema(description = "分配时间检索")
    private String[] cusAssignSalesTimeRangeArray;
    @Schema(description = "是否分配服务人员")
    private CusAssignServersStatusCodeEnum cusAssignServersStatusCode;

    /**
     * 微信号
     */
    @Schema(description = "微信号")
    private String cusWechatId;
    /**
     * 客户绑定的系统用户编码（表字段 cus_user_code）
     */
    @Schema(description = "客户用户号")
    @TableField("cus_user_code")
    private String cusUserCode;

    /**
     * 是否成交
     */
    @Schema(description = "是否成交")
    private CusDealtStatusCodeEnum cusDealtStatusCode;
    /**
     * 是否完单
     */
    @Schema(description = "是否完单")
    private CusDealtCompleteStatusCodeEnum cusDealtCompleteStatusCode;

    /**
     * 是否跟进
     */
    @Schema(description = "是否跟进")
    private CusFollowUpStatusCodeEnum cusFollowUpStatusCode;
    @Schema(description = "跟进提醒类型")
    private CusFollowUpReminderTypeCodeEnum cusFollowUpReminderTypeCode;

}
