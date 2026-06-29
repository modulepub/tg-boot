package pub.module.affines.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;
import pub.module.system.api.constants.UserSexCodeEnum;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 孩子资料卡
 */
@Data
@TableName("af_child_profile")
@EqualsAndHashCode(callSuper = false)
@Schema(description = "孩子资料卡")
public class AfChildProfile extends BaseEntity {

    @Schema(description = "孩子资料卡编码")
    private String afChildProfileCode;

    @Schema(description = "家长用户编码")
    private String afParentUserCode;

    @Schema(description = "孩子姓名")
    private String afChildName;

    @Schema(description = "孩子昵称")
    private String afChildNickName;

    @Schema(description = "孩子性别")
    private UserSexCodeEnum afChildSexCode;

    @Schema(description = "孩子年龄")
    private Integer afChildAge;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "孩子生日")
    private Date afChildBirthday;

    @Schema(description = "身高(cm)")
    private Integer afChildHeight;

    @Schema(description = "体重(kg)")
    private Integer afChildWeight;

    @Schema(description = "学历编码")
    private String afChildEducationCode;

    @Schema(description = "学历名称")
    private String afChildEducationName;

    @Schema(description = "婚姻状况")
    private StatusCodeEnum afChildMaritalStatusCode;

    @Schema(description = "是否二婚")
    private StatusCodeEnum afChildRemarriageStatusCode;

    @Schema(description = "是否残疾")
    private StatusCodeEnum afChildDisabledStatusCode;

    @Schema(description = "生活城市编码")
    private String afChildCityResidenceCode;

    @Schema(description = "生活城市名称")
    private String afChildCityResidenceName;

    @Schema(description = "职业描述")
    private String afChildOccupationalDescription;

    @Schema(description = "头像")
    private String afChildAvatar;

    @Schema(description = "生活照")
    private String afChildLifePhoto;

    @Schema(description = "是否有车")
    private StatusCodeEnum afChildHaveCarStatusCode;

    @Schema(description = "是否有房")
    private StatusCodeEnum afChildHaveHouseStatusCode;

    @Schema(description = "年收入")
    private BigDecimal afChildAnnualIncomeAmount;

    @Schema(description = "简要描述")
    private String afChildDesc;

    @Schema(description = "是否隐藏")
    private StatusCodeEnum afChildHiddenStatusCode;

    @Schema(description = "是否发布")
    private StatusCodeEnum afChildPublishStatusCode;
}
