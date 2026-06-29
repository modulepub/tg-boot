package pub.module.system.api.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.system.api.constants.SysOrgCategoryCodeEnum;

import java.util.List;

/**
 * 组织机构 对象
 *
 * @author tg
 * 2025-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "组织机构")
public class OrganizationDTO {

    private String id;
    @Schema(description = "机构编码")
    private String orgCode;

    @Schema(description = "机构/部门名称")
    private String orgName;

    @Schema(description = "父机构编码")
    private String orgParentCode;

    @Schema(description = "排序")
    private Long seqNo;

    @Schema(description = "描述")
    private String orgDescription;

    @Schema(description = "机构类别 公司 com，组织机构 depart，岗位 post")
    private SysOrgCategoryCodeEnum orgCategoryCode;
    @Schema(description = "子机构")
    List<OrganizationDTO> children;


}
