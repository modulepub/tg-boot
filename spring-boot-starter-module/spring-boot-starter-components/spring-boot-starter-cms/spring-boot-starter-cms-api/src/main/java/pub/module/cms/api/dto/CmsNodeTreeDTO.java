package pub.module.cms.api.dto;

import pub.module.common.enums.StatusCodeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import pub.module.cms.api.constants.NodeContentTypeCodeEnum;
import pub.module.cms.api.constants.NodeTypeCodeEnum;

import java.util.Date;
import java.util.List;

@Data
public class CmsNodeTreeDTO {
    String id;
    @Schema(description = "父级编码")
    private String nodeParentCode;

    @Schema(description = "内容编码")
    private String nodeCode;

    @Schema(description = "名称")
    private String nodeName;

    @Schema(description = "头图")
    private String nodeHeadImg;

    @Schema(description = "摘要")
    private String nodeSummary;

        @JsonFormat(pattern = "yyyy-MM-dd")
@Schema(description = "发布时间")
    private Date nodePublishTime;

    @Schema(description = "发布状态")
    private StatusCodeEnum nodePublishStatusCode;

    @Schema(description = "节点类型")
    private NodeTypeCodeEnum nodeTypeCode;

    @Schema(description = "内容类型")
    private NodeContentTypeCodeEnum nodeContentTypeCode;

    @Schema(description = "链接")
    private String nodeLink;

    @TableField(exist = false)
    List<CmsNodeTreeDTO> children;

}
