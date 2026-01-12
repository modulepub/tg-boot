package pub.module.dating.curd.entity;

    import java.util.Date;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.data.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 推荐 对象
 * @author tg
 * 2026-01-07 23:30:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "推荐")
public class DtRecommended extends BaseEntity {
                    /** 所属部门 */
                        @Schema(description = "所属部门")
                private String sysOrgCode;

                    /** 客户编码 */
                        @Schema(description = "客户编码")
                private String rcSysUserCode;

                    /** 被推荐的客户 */
                        @Schema(description = "被推荐的客户")
                private String rcToSysUserCode;

                    /** 推荐时间 */
                        @JsonFormat(pattern = "yyyy-MM-dd")
                        @Schema(description = "推荐时间")
                private Date rcTime;

                    /** 意向 ID */
                        @Schema(description = "意向 ID")
                private String intentionCode;


        }
