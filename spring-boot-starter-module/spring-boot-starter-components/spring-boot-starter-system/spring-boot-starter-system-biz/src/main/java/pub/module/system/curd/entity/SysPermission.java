package pub.module.system.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.api.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

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
    /**
     * 父菜单编码
     */
    @Schema(description = "父菜单编码")
    private String perParentCode;

    /**
     * 菜单编码
     */
    @Schema(description = "菜单编码")
    private String perCode;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String perName;

    /**
     * 菜单 URL
     */
    @Schema(description = "菜单 URL")
    private String perUrl;


    /**
     * 类型   0：菜单   1：按钮   2：接口
     */
    @Schema(description = "类型   0：菜单   1：按钮   2：接口")
    private String perTypeCode;

    /**
     * 打开方式   0：内部   1：外部
     */
    @Schema(description = "打开方式   0：内部   1：外部")
    private String perOpenStyleCode;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String perIcon;


}
