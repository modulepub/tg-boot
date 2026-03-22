package pub.module.system.curd.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.api.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 组织机构表 对象
 * @author tg
 * 2026-01-04 13:16:22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "组织机构表")
public class SysOrganization extends BaseEntity {
                    /** 机构/部门名称 */
                        @Schema(description = "机构/部门名称")
                private String orgName;

                    /** 父机构编码 */
                        @Schema(description = "父机构编码")
                private String orgParentCode;

                    /** 描述 */
                        @Schema(description = "描述")
                private String orgDescription;

                    /** 机构类别 公司 com，组织机构 depart */
                        @Schema(description = "机构类别 公司 com，组织机构 depart")
                private String orgCategoryCode;


        }
