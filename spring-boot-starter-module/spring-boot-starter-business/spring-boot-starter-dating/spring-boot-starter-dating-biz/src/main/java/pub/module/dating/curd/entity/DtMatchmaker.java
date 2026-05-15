package pub.module.dating.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;

import java.math.BigDecimal;

/**
 * 红娘信息 对象
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "红娘信息")
public class DtMatchmaker extends BaseEntity {
    /**
     * 用户号
     */
    @Schema(description = "用户号")
    private String mkUserCode;

    private String mkCode;


    @Schema(description = "工作照")
    private String mkWorkPhoto;

    @Schema(description = "红娘姓名")
    private String mkName;

    @Schema(description = "年龄")
    private Integer mkAge;

    @Schema(description = "服务人数")
    private Long mkServiceUserCount;
    @Schema(description = "电话")
    private String mkPhone;
    @Schema(description = "红娘标签")
    private String mkTags;
    /**
     * 证件号
     */
    @Schema(description = "证件号")
    private String mkIdNo;

    @Schema(description = "所属城市")
    private String mkCityCode;
    private String mkCityName;

    /**
     * 婚介所编码
     */
    @Schema(description = "婚介所编码")
    private String mkCompanyCode;

    /**
     * 婚介所名称
     */
    @Schema(description = "婚介所名称")
    private String mkCompanyName;

    @Schema(description = "说说")
    private String mkMoment;
    @Schema(description = "认证状态")
    private String mkIdentityStatusCode;
    @Schema(description = "评分")
    private BigDecimal mkScore;


}
