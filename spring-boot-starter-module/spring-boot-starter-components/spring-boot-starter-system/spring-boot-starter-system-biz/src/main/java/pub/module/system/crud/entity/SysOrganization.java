package pub.module.system.crud.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.system.api.constants.SysOrgCategoryCodeEnum;

/**
 * 组织机构表 对象
 * @author tg
 * 2026-01-04 13:16:22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "组织机构表")
public class SysOrganization extends BaseEntity {
                    @Schema(description = "机构/部门名称")
                private String orgName;

                    @Schema(description = "父机构编码")
                private String orgParentCode;

                    @Schema(description = "描述")
                private String orgDescription;

                    @Schema(description = "机构类别 公司 com，组织机构 depart")
                private SysOrgCategoryCodeEnum orgCategoryCode;


        }
