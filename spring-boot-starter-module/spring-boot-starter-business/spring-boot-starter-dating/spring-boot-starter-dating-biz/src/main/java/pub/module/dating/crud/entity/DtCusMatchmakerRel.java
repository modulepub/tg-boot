package pub.module.dating.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
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
    private StatusCodeEnum cusMkRelShowStatusCode;

    private String mkCode;

    @Schema(description = "红娘用户号")
    private String mkUserCode;

    @Schema(description = "电话")
    private String mkPhone;
    @Schema(description = "工作照")
    private String mkWorkPhoto;

    @Schema(description = "客户姓名")
    private String mkName;

    @Schema(description = "证件号")
    private String mkIdNo;

    @Schema(description = "婚介所编码")
    private String mkCompanyCode;

    @Schema(description = "婚介所名称")
    private String mkCompanyName;

    @Schema(description = "所在城市编码")
    private String mkCityCode;

    @Schema(description = "所在城市名称")
    private String mkCityName;

    @Schema(description = "说说")
    private String mkMoment;

    @Schema(description = "红娘认证状态")
    private StatusCodeEnum mkIdentityStatusCode;

    @Schema(description = "评分")
    private BigDecimal mkScore;

    @Schema(description = "客户编码")
    private String cusCode;

    @Schema(description = "客户姓名")
    private String cusName;

    @Schema(description = "客户昵称")
    private String cusNickName;

    @Schema(description = "实名认证状态")
    private StatusCodeEnum cusIdentityAuthenticatedStatusCode;

    @Schema(description = "客户头像")
    private String cusAvatar;

    @Schema(description = "客户性别")
    private UserSexCodeEnum cusSexCode;

    @Schema(description = "客户说说")
    private String cusMoment;

    @Schema(description = "是否隐藏（冗余自客户表 cus_hidden_status_code，1 隐藏 0 不隐藏）")
    private StatusCodeEnum cusHiddenStatusCode;

    @Schema(description = "客户手机号")
    private String cusPhone;

    @Schema(description = "是否测试数据（StatusCode：1是 0否）")
    private StatusCodeEnum cusMkRelTestStatusCode;

}
