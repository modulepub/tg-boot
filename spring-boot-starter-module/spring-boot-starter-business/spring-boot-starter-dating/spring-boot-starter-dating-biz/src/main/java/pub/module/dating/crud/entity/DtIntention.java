package pub.module.dating.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.dating.api.constants.CusKinshipCodeEnum;
import pub.module.dating.api.constants.*;
import pub.module.system.api.constants.UserSexCodeEnum;

/**
 * 交友意向 对象
 *
 * @author tg
 * 2026-01-07 23:30:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "交友意向")
public class DtIntention extends BaseEntity {


    @Schema(description = "用户")
    private String intentionUserCode;
    @Schema(description = "编码")
    private String intentionCode;

    @Schema(description = "是否同意")
    private StatusCodeEnum intentionAgreeStatusCode;

    @Schema(description = "名称")
    private String intentionName;

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


    @Schema(description = "期望嘉宾性别：与字典一致（如 1 男 2 女）。找女婿与找男朋友同为男嘉宾；找儿媳与找女朋友同为女嘉宾。")
    private UserSexCodeEnum intentionSexCode;

    @Schema(description = "是否接受异地：1 接受，0 希望同城")
    private StatusCodeEnum intentionLdrStatusCode;

    @Schema(description = "是否接受残疾")
    private StatusCodeEnum intentionDisabledStatusCode;

    @Schema(description = "高学历优先：1 是，0 否")
    private StatusCodeEnum intentionHigherEducationStatusCode;

    @Schema(description = "红娘助力：1 是，0 否")
    private StatusCodeEnum intentionSupportStatusCode;
    @Schema(description = "客户身份编码：self 本人，parent（界面展示家长）")
    private CusKinshipCodeEnum cusKinshipCode;

    @TableField(exist = false)
    @Schema(description = "访客预览嘉宾编码（仅登录后首次同步推荐时使用，不落库）")
    private String guestPreviewCusCode;


}
