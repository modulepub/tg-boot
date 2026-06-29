package pub.module.dating.crud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.api.constants.CusKinshipCodeEnum;
import pub.module.dating.api.constants.ContactApplySourceCodeEnum;
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
                    @Schema(description = "用户编码")
                private String userCode;

                    @Schema(description = "申请编码")
                private String contactApplyCode;

                    @Schema(description = "联系人来源")
                private ContactApplySourceCodeEnum contactApplySourceCode;

                    @Schema(description = "是否通过")
                private StatusCodeEnum contactApplyPassedStatusCode;

                    @Schema(description = "打招呼")
                private String contactApplyGreeting;

                    @Schema(description = "客户编号")
                private String cusCode;

                    @Schema(description = "头像")
                private String cusAvatar;

                    @Schema(description = "客户生活照")
                private String cusLifePhoto;

                    @Schema(description = "客户常驻地点")
                private String cusResidenceLngLat;

                    @Schema(description = "客户姓名")
                private String cusName;

                    @Schema(description = "客户性别")
                private UserSexCodeEnum cusSexCode;

                    @Schema(description = "年龄")
                private Long cusAge;

                    @Schema(description = "身高(cm)")
                private Long cusHeight;

                    @Schema(description = "体重")
                private Long cusWeight;

    @Schema(description = "客户身份编码：self 本人，parent 家长（与客户表同名字段冗余）")
    private CusKinshipCodeEnum cusKinshipCode;

    @Schema(description = "生活城市名称（与客户表同名字段冗余）")
    private String cusCityResidenceName;

    @Schema(description = "说说（与客户表同名字段冗余）")
    private String cusMoment;

    @Schema(description = "手机号（与客户表同名字段冗余）")
    private String cusPhone;

        @TableField("cus_user_code")
@Schema(description = "被申请人客户绑定用户冗余（DtCustomer.cus_user_code）")
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
    private CusKinshipCodeEnum appCusKinshipCode;

    @Schema(description = "申请人生活城市名称冗余")
    private String appCusCityResidenceName;

    @Schema(description = "申请人说说冗余")
    private String appCusMoment;

    @Schema(description = "申请人手机号冗余")
    private String appCusPhone;

}
