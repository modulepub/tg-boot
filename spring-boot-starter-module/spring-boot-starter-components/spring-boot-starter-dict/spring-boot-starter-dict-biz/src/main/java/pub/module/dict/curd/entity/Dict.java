package pub.module.dict.curd.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description="字典实体")
public class Dict extends BaseEntity implements Serializable {

    /**
     * 字典名称
     */
    @Schema(description="字典名称")
    private String dictName;

    /**
     * 字典编码
     */
    @Schema(description="字典编码")
    private String dictCode;

}
