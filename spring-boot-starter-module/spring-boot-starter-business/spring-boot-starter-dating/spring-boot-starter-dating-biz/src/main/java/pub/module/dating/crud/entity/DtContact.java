package pub.module.dating.crud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.api.constants.CusKinshipCodeEnum;
import pub.module.dating.api.constants.ContactSourceCodeEnum;
import pub.module.system.api.constants.UserSexCodeEnum;

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
    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "联系编码")
    private String contactCode;

    @Schema(description = "申请编码")
    private String contactApplyCode;

    @Schema(description = "联系人来源")
    private ContactSourceCodeEnum contactSourceCode;

    @Schema(description = "打招呼")
    private String contactApplyGreeting;
    /**
     * 客户编号
     */
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

    @Schema(description = "客户身份编码：self 本人，parent 家长")
    private CusKinshipCodeEnum cusKinshipCode;

    @Schema(description = "生活城市名称")
    private String cusCityResidenceName;

    @Schema(description = "说说")
    private String cusMoment;

    @Schema(description = "手机号")
    private String cusPhone;

        @TableField("cus_user_code")
@Schema(description = "对方客户绑定用户编码冗余（DtCustomer.cus_user_code）")
    private String cusUserCode;


}
