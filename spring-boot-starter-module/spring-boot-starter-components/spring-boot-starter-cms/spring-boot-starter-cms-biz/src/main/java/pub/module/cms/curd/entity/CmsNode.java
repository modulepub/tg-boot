package pub.module.cms.curd.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;
import pub.module.data.api.entity.BaseEntity;

/**
 * CMS-节点 对象
 *
 * @author tg
 * 2026-03-08 16:04:07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "CMS-节点")
public class CmsNode extends BaseEntity {
    /**
     * 父级编码
     */
    @Schema(description = "父级编码")
    private String nodeParentCode;

    /**
     * 内容编码
     */
    @Schema(description = "内容编码")
    private String nodeCode;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String nodeName;

    /**
     * 头图
     */
    @Schema(description = "头图")
    private String nodeHeadImg;

    /**
     * 摘要
     */
    @Schema(description = "摘要")
    private String nodeSummary;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "发布时间")
    private Date nodePublishTime;

    /**
     * 发布状态
     */
    @Schema(description = "发布状态")
    private String nodePublishStatusCode;

    /**
     * 节点类型（catalog栏目、document文章）
     */
    @Schema(description = "节点类型")
    private String nodeTypeCode;

    /**
     * 内容类型（text、文本、link、链接、citation、引用）
     */
    @Schema(description = "内容类型")
    private String nodeContentTypeCode;

    /**
     * 链接
     */
    @Schema(description = "链接")
    private String nodeLink;

    /**
     * 正文
     */
    @Schema(description = "正文")
    private String nodeContent;


}
