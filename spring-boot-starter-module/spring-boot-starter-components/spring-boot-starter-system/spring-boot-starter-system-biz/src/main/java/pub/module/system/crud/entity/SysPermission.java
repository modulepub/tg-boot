package pub.module.system.crud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.system.api.constants.PerOpenStyleCodeEnum;
import pub.module.system.api.constants.PerTypeCodeEnum;

/**
 * 菜单管理 对象
 *
 * @author tg
 * 2026-01-04 13:16:23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "菜单管理")
public class SysPermission extends BaseEntity {
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


}
