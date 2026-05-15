package pub.module.dating.curd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

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
     * 是否残疾
     */
    @Schema(description = "是否接受残疾")
    private String intentionDisabledStatusCode;

}
