package pub.module.dating.api.service.dto;

import pub.module.common.enums.StatusCodeEnum;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.dating.api.constants.CusKinshipCodeEnum;
import pub.module.dating.api.constants.*;
import pub.module.system.api.constants.UserSexCodeEnum;

 /**
  * 相亲意向
  * @author tg
  * @since 2025-06-15
  * @version V1.0
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class DtIntentionDTO implements Serializable {

     @Schema(description = "主键，编辑时必填")
     private String id;

     @Schema(description = "用户")
     private String intentionUserCode;
     @Schema(description = "编码")
     private String intentionCode;

     @Schema(description = "名称")
     private String intentionName;

     @Schema(description = "最大年龄")
     private Integer intentionMaxAge;

     @Schema(description = "最小年龄")
     private Integer intentionMinAge;

     @Schema(description = "是否有房")
     private StatusCodeEnum intentionHaveHouseCode;

      @Schema(description = "是否同意")
     private StatusCodeEnum intentionAgreeStatusCode;

     @Schema(description = "是否有车")
     private StatusCodeEnum intentionHaveCarCode;




     @Schema(description = "城市")
     private String intentionCityCode;


     @Schema(description = "期望嘉宾性别：1 男 2 女（找女婿=男同找男友；找儿媳=女同找女友）")
     private UserSexCodeEnum intentionSexCode;

     @Schema(description = "是否接受异地：1 接受，0 希望同城")
     private StatusCodeEnum intentionLdrStatusCode;

     @Schema(description = "是否接受残疾")
     private StatusCodeEnum intentionDisabledStatusCode;

     @Schema(description = "高学历优先：1 是，0 否")
     private StatusCodeEnum intentionHigherEducationStatusCode;

     @Schema(description = "红娘助力：1 是，0 否")
     private StatusCodeEnum intentionSupportStatusCode;

     @Schema(description = "客户身份编码：self 本人，parent 家长")
     private CusKinshipCodeEnum cusKinshipCode;

}
