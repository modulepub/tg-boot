package pub.module.dating.api.service.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
     private String intentionHaveHouseCode;

      @Schema(description = "是否同意")
     private String intentionAgreeStatusCode;

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
