package pub.module.dating.curd.entity;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;
import pub.module.system.api.constants.UserSexCodeEnum;

/**
 * 客户红娘关系 对象
 *
 * @author tg
 * 2026-03-25 00:36:20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户红娘关系")
public class DtCusMatchmakerRel extends BaseEntity {

    @Schema(description = "编码")
    private String cusMkRelCode;

    @Schema(description = "是否显示红娘主页")
    private String cusMkRelShowStatusCode;

    private String mkCode;

    /**
     * 红娘系统用户编码（与 dt_matchmaker.mk_user_code 一致）
     */
    @Schema(description = "红娘用户号")
    private String mkUserCode;

    @Schema(description = "电话")
    private String mkPhone;
    /**
     * 工作照
     */
    @Schema(description = "工作照")
    private String mkWorkPhoto;

    /**
     * 客户姓名
     */
    @Schema(description = "客户姓名")
    private String mkName;

    /**
     * 证件号
     */
    @Schema(description = "证件号")
    private String mkIdNo;

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

    /**
     * 所在城市编码
     */
    @Schema(description = "所在城市编码")
    private String mkCityCode;

    /**
     * 所在城市名称
     */
    @Schema(description = "所在城市名称")
    private String mkCityName;

    /**
     * 说说
     */
    @Schema(description = "说说")
    private String mkMoment;

    /**
     * 红娘认证状态
     */
    @Schema(description = "红娘认证状态")
    private String mkIdentityStatusCode;

    /**
     * 评分
     */
    @Schema(description = "评分")
    private BigDecimal mkScore;

    /**
     * 客户编码
     */
    @Schema(description = "客户编码")
    private String cusCode;

    /**
     * 客户姓名
     */
    @Schema(description = "客户姓名")
    private String cusName;

    /**
     * 客户头像
     */
    @Schema(description = "客户头像")
    private String cusAvatar;

    @Schema(description = "客户性别")
    private UserSexCodeEnum cusSexCode;

    /**
     * 客户说说
     */
    @Schema(description = "客户说说")
    private String cusMoment;


}
