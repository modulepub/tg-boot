package pub.module.affines.api.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.system.api.constants.UserSexCodeEnum;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "孩子意向对象条件")
public class AfChildIntentionDTO {

    @Schema(description = "意向编码")
    private String afChildIntentionCode;

    @Schema(description = "孩子资料卡编码")
    private String afChildProfileCode;

    @Schema(description = "意向最小年龄")
    private Integer afIntentionMinAge;

    @Schema(description = "意向最大年龄")
    private Integer afIntentionMaxAge;

    @Schema(description = "期望嘉宾性别")
    private UserSexCodeEnum afIntentionSexCode;

    @Schema(description = "是否有房")
    private StatusCodeEnum afIntentionHaveHouseCode;

    @Schema(description = "是否有车")
    private StatusCodeEnum afIntentionHaveCarCode;

    @Schema(description = "意向城市编码")
    private String afIntentionCityCode;

    @Schema(description = "是否接受异地")
    private StatusCodeEnum afIntentionLdrStatusCode;

    @Schema(description = "是否接受残疾")
    private StatusCodeEnum afIntentionDisabledStatusCode;

    @Schema(description = "高学历优先")
    private StatusCodeEnum afIntentionHigherEducationStatusCode;
}
