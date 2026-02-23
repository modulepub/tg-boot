package pub.module.system.api.service.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
  * 菜单表
  * @author tg
  * @since 2025-06-11
  * @version V1.0
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="菜单")
public class PermissionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 父菜单编码 */
    @Schema(description = "父菜单编码")
    private String perParentCode;

    /** 菜单编码 */
    @Schema(description = "菜单编码")
    private String perCode;

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String perName;

    /** 菜单 URL */
    @Schema(description = "菜单 URL")
    private String perUrl;

    /** 类型   0：菜单   1：按钮   2：接口 */
    @Schema(description = "类型   0：菜单   1：按钮   2：接口")
    private String perTypeCode;

    /** 打开方式   0：内部   1：外部 */
    @Schema(description = "打开方式   0：内部   1：外部")
    private String perOpenStyleCode;

    /** 菜单图标 */
    @Schema(description = "菜单图标")
    private String perIcon;

    @TableField(exist = false)
    private java.util.List<PermissionDTO> children;
}
