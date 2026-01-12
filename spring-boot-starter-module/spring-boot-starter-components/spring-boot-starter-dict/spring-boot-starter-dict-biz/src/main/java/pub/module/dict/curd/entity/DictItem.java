package pub.module.dict.curd.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.data.entity.BaseEntity;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="字典值实体")
public class DictItem extends BaseEntity implements Serializable {


    /**
     * 字典编码
     */
    @Schema(description="字典编码")
    private String dictCode;

    /**
     * 编码
     */
    @Schema(description="编码")
    private String dictItemCode;

    /**
     * 字典项文本
     */
    @Schema(description="字典项文本")
    private String dictItemText;

    /**
     * 字典项值
     */
    @Schema(description="字典项值")
    private String dictItemValue;

    /**
     * 描述
     */
    @Schema(description="描述")
    private String dictItemDescription;


    /**
     * 字典项颜色 
     */
    @Schema(description="字典项颜色")
    private String dictItemColor;

}
