package pub.module.dating.api.service.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.customer.api.constants.CusHaveCarStatusCodeEnum;
import pub.module.customer.api.constants.CusHaveHouseStatusCodeEnum;
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

     /**
      * 用户
      */
     @Schema(description = "用户")
     private String intentionUserCode;
     /**
      * 编码
      */
     @Schema(description = "编码")
     private String intentionCode;

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

      @Schema(description = "是否同意")
     private String intentionAgreeStatusCode;

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
     @Schema(description = "期望嘉宾性别：1 男 2 女（找女婿=男同找男友；找儿媳=女同找女友）")
     private UserSexCodeEnum intentionSexCode;

     /**
      * 是否接受异地
      */
     @Schema(description = "是否接受异地：1 接受，0 希望同城")
     private String intentionLdrStatusCode;

     /**
      * 是否残疾
      */
     @Schema(description = "是否接受残疾")
     private String intentionDisabledStatusCode;

     @Schema(description = "高学历优先：1 是，0 否")
     private String intentionHigherEducationStatusCode;

     @Schema(description = "红娘助力：1 是，0 否")
     private String intentionSupportStatusCode;

     /**
      * 客户身份（与 Customer.cusKinshipCode 一致：self 本人，parent 家长）
      */
     @Schema(description = "客户身份编码：self 本人，parent 家长")
     private String cusKinshipCode;

}
