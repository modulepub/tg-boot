package pub.module.dating.curd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;

/**
 * 联系人 对象
 *
 * @author tg
 * 2026-05-01 23:01:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "联系人")
public class DtContact extends BaseEntity {
    /**
     * 用户编码
     */
    @Schema(description = "用户编码")
    private String userCode;

    /**
     * 联系编码
     */
    @Schema(description = "联系编码")
    private String contactCode;

    /** 申请编码 */
    @Schema(description = "申请编码")
    private String contactApplyCode;

    /**
     * 联系人来源
     */
    @Schema(description = "联系人来源")
    private String contactSourceCode;

    @Schema(description = "打招呼")
    private String contactApplyGreeting;
    /**
     * 客户编号
     */
    private String cusCode;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String cusAvatar;

    /**
     * 客户生活照
     */
    @Schema(description = "客户生活照")
    private String cusLifePhoto;

    /**
     * 客户常驻地点
     */
    @Schema(description = "客户常驻地点")
    private String cusResidenceLngLat;

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

    /** 客户身份（与客户表 cus_kinship_code 同名字段冗余） */
    @Schema(description = "客户身份编码：self 本人，parent 家长")
    private String cusKinshipCode;

    /** 常驻城市名称（与客户表同名字段冗余） */
    @Schema(description = "常驻城市名称")
    private String cusCityResidenceName;

    /** 说说（与客户表同名字段冗余） */
    @Schema(description = "说说")
    private String cusMoment;

    /** 手机号（与客户表同名字段冗余） */
    @Schema(description = "手机号")
    private String cusPhone;

    /**
     * 对方客户绑定用户编码冗余（对应 {@code customer.cus_user_code}；本实体 {@code userCode} 为列表归属用户）
     */
    @TableField("cus_user_code")
    @Schema(description = "对方客户绑定用户编码冗余（customer.cus_user_code）")
    private String cusUserCode;

}
