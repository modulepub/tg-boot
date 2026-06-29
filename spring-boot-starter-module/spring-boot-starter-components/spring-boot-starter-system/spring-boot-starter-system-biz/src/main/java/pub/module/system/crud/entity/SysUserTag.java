package pub.module.system.crud.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

/**
 * 用户标签 对象
 *
 * @author tg
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户标签")
public class SysUserTag extends BaseEntity {

    @Schema(description = "业务主键")
    private String userTagCode;

    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "标签名称")
    private String tagName;
}
