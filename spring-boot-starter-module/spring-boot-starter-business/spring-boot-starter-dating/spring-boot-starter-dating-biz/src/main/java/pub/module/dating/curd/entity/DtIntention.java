package pub.module.dating.curd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.customer.api.constants.CusHaveCarStatusCodeEnum;
import pub.module.customer.api.constants.CusHaveHouseStatusCodeEnum;
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


    /**
     * 用户（库列 intention_sys_user_code）
     */
    @Schema(description = "用户")
    private String intentionUserCode;
    /**
     * 编码
     */
    @Schema(description = "编码")
    private String intentionCode;

    @Schema(description = "是否同意")
    private String intentionAgreeStatusCode;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String intentionName;

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
    private CusHaveHouseStatusCodeEnum intentionHaveHouseCode;

    /**
     * 是否有车
     */
    @Schema(description = "是否有车")
    private CusHaveCarStatusCodeEnum intentionHaveCarCode;




    /**
     * 城市
     */
    @Schema(description = "城市")
    private String intentionCityCode;


    /**
     * 性别
     */
    @Schema(description = "期望嘉宾性别：与字典一致（如 1 男 2 女）。找女婿与找男朋友同为男嘉宾；找儿媳与找女朋友同为女嘉宾。")
    private UserSexCodeEnum intentionSexCode;

    /**
     * 是否接受异地（long-distance relationship）
     */
    @Schema(description = "是否接受异地：1 接受，0 希望同城")
    private String intentionLdrStatusCode;

    /**
     * 是否接受残疾
     */
    @Schema(description = "是否接受残疾")
    private String intentionDisabledStatusCode;

    /**
     * 高学历优先（1 是 0 否）
     */
    @Schema(description = "高学历优先：1 是，0 否")
    private String intentionHigherEducationStatusCode;

    /**
     * 红娘助力（1 是 0 否）
     */
    @Schema(description = "红娘助力：1 是，0 否")
    private String intentionSupportStatusCode;
    /**
     * 客户身份（字典：self 本人；parent 客户端展示为「家长」）
     */
    @Schema(description = "客户身份编码：self 本人，parent（界面展示家长）")
    private String cusKinshipCode;


}
