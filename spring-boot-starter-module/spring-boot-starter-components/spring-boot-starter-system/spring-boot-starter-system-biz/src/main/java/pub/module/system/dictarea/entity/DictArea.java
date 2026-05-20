package pub.module.system.dictarea.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

import java.io.Serializable;

/**
 * 国际/国内地区字典，供前端生活城市等下拉与搜索。（由 system-biz 提供，替代原 dict 模块）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(description = "地区字典实体")
@TableName("dict_area")
public class DictArea extends BaseEntity implements Serializable {

    @Schema(description = "地区业务编码（如国家 ISO、行政区划码）")
    private String dictAreaCode;

    @Schema(description = "父级地区业务编码，根节点为空")
    private String dictAreaParentCode;

    @Schema(description = "层级：1 国家/地区 2 省或一级行政区 3 城市或下级")
    private Integer dictAreaLevel;

    @Schema(description = "中文名称")
    private String dictAreaName;

    @Schema(description = "英文名称")
    private String dictAreaNameEn;

    @Schema(description = "完整路径文案，用于展示与关键词检索")
    private String dictAreaFullName;
}
