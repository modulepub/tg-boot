package pub.module.cms.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_node_read_record")
@Schema(description = "CMS-文章阅读记录")
public class CmsNodeReadRecord extends BaseEntity {

    @Schema(description = "阅读记录编码")
    private String nodeReadRecordCode;

    @Schema(description = "文章节点编码")
    private String nodeCode;

    @Schema(description = "单次阅读会话编码")
    private String nodeReadRecordSessionCode;

    @Schema(description = "阅读用户编码（登录时）")
    private String nodeReadRecordUserCode;

    @Schema(description = "客户端IP")
    private String nodeReadRecordClientIp;

    @Schema(description = "IP归属地")
    private String nodeReadRecordIpLocation;

    @Schema(description = "阅读进度 0-100")
    private Integer nodeReadRecordProgress;
}
