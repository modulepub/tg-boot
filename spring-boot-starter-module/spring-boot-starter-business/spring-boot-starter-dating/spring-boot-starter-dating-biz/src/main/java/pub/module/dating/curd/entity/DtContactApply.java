package pub.module.dating.curd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.api.constants.RelationPassedStatusCodeEnum;
import pub.module.system.api.constants.UserSexCodeEnum;

/**
 * 联系人申请表 对象
 * @author tg
 * 2026-05-03 03:39:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "联系人申请表")
public class DtContactApply extends BaseEntity {
                    /** 用户编码 */
                        @Schema(description = "用户编码")
                private String userCode;

                    /** 申请编码 */
                        @Schema(description = "申请编码")
                private String contactApplyCode;

                    /** 联系人来源 */
                        @Schema(description = "联系人来源")
                private String contactApplySourceCode;

                    /** 是否通过 */
                        @Schema(description = "是否通过")
                private RelationPassedStatusCodeEnum contactApplyPassedStatusCode;

                    /** 打招呼 */
                        @Schema(description = "打招呼")
                private String contactApplyGreeting;

                    /** 客户编号 */
                        @Schema(description = "客户编号")
                private String cusCode;

                    /** 头像 */
                        @Schema(description = "头像")
                private String cusAvatar;

                    /** 客户生活照 */
                        @Schema(description = "客户生活照")
                private String cusLifePhoto;

                    /** 客户常驻地点 */
                        @Schema(description = "客户常驻地点")
                private String cusResidenceLngLat;

                    /** 客户姓名 */
                        @Schema(description = "客户姓名")
                private String cusName;

                    /** 客户性别 */
                        @Schema(description = "客户性别")
                private UserSexCodeEnum cusSexCode;

                    /** 年龄 */
                        @Schema(description = "年龄")
                private Long cusAge;

                    /** 身高(cm) */
                        @Schema(description = "身高(cm)")
                private Long cusHeight;

                    /** 体重（kg) */
                        @Schema(description = "体重")
                private Long cusWeight;

    @Schema(description = "客户身份编码：self 本人，parent 家长（与客户表同名字段冗余）")
    private String cusKinshipCode;

    @Schema(description = "生活城市名称（与客户表同名字段冗余）")
    private String cusCityResidenceName;

    @Schema(description = "说说（与客户表同名字段冗余）")
    private String cusMoment;

    @Schema(description = "手机号（与客户表同名字段冗余）")
    private String cusPhone;

    /** 被申请人客户绑定用户冗余（对应 {@code customer.cus_user_code}；{@code userCode} 为申请人） */
    @TableField("cus_user_code")
    @Schema(description = "被申请人客户绑定用户冗余（customer.cus_user_code）")
    private String cusUserCode;

    @Schema(description = "申请人客户编号冗余")
    private String appCusCode;

    @Schema(description = "申请人头像冗余")
    private String appCusAvatar;

    @Schema(description = "申请人姓名冗余")
    private String appCusName;

    @Schema(description = "申请人性别冗余")
    private UserSexCodeEnum appCusSexCode;

    @Schema(description = "申请人年龄冗余")
    private Long appCusAge;

    @Schema(description = "申请人身份编码：self 本人，parent 家长")
    private String appCusKinshipCode;

    @Schema(description = "申请人生活城市名称冗余")
    private String appCusCityResidenceName;

    @Schema(description = "申请人说说冗余")
    private String appCusMoment;

    @Schema(description = "申请人手机号冗余")
    private String appCusPhone;

}
