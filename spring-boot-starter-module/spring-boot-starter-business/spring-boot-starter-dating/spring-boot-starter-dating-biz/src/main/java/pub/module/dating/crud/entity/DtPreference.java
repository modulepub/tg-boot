package pub.module.dating.crud.entity;

import pub.module.common.enums.StatusCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.common.model.po.BaseEntity;
import pub.module.dating.api.constants.DtLikeDegreeCodeEnum;

/**
 * 偏好 对象
 *
 * @author tg
 * 2026-03-31 02:10:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "偏好")
public class DtPreference extends BaseEntity {

    @Schema(description = "编码")
    private String preferenceCode;
    @Schema(description = "名称")
    private String preferenceName;

    @Schema(description = "客户编码")
    private String preferenceCusCode;
    @Schema(description = "客户姓名")
    private String preferenceCusName;
    @Schema(description = "客户年龄")
    private Integer preferenceCusAge;
    @Schema(description = "客户头像")
    private String preferenceCusAvatar;
    @Schema(description = "客户生活城市")
    private String preferenceCusCityResidenceCode;
    @Schema(description = "客户生活城市名称")
    private String preferenceCusCityResidenceName;

    @Schema(description = "目标对象客户编码")
    private String preferenceTargetCusCode;
    @Schema(description = "目标对象姓名")
    private String preferenceTargetCusName;
    @Schema(description = "目标对象年龄")
    private Integer preferenceTargetCusAge;
    @Schema(description = "目标对象头像")
    private String preferenceTargetCusAvatar;
    @Schema(description = "目标对象生活城市")
    private String preferenceTargetCusCityResidenceCode;
    @Schema(description = "目标对象生活城市名称")
    private String preferenceTargetCusCityResidenceName;
    @Schema(description = "是否喜欢")
    private DtLikeDegreeCodeEnum preferenceLikeStatusCode;
    @Schema(description = "是否相互喜欢：1 双向喜欢，0 否")
    private StatusCodeEnum preferenceMutuaStatusCode;
}
