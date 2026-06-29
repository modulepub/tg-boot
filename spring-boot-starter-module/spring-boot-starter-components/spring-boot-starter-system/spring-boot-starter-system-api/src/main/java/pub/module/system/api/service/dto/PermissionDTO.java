package pub.module.system.api.service.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.system.api.constants.PerOpenStyleCodeEnum;
import pub.module.system.api.constants.PerTypeCodeEnum;

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
    @Schema(description = "id")
    private String id;
    @Schema(description = "序号")
    private Long seqNo;
    @Schema(description = "父菜单编码")
    private String perParentCode;

    @Schema(description = "菜单编码")
    private String perCode;

    @Schema(description = "菜单名称")
    private String perName;

    @Schema(description = "菜单 URL")
    private String perUrl;

    @Schema(description = "类型   0：菜单   1：按钮   2：接口")
    private PerTypeCodeEnum perTypeCode;

    @Schema(description = "打开方式   0：内部   1：外部")
    private PerOpenStyleCodeEnum perOpenStyleCode;

    @Schema(description = "菜单图标")
    private String perIcon;

    @TableField(exist = false)
    private java.util.List<PermissionDTO> children;
}
