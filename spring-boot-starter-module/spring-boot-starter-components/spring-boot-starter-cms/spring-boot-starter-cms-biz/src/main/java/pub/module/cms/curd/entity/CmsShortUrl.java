package pub.module.cms.curd.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.cms.api.constants.ShortUrlStatusCodeEnum;
import pub.module.common.model.po.BaseEntity;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_short_url")
@Schema(description = "CMS-短链")
public class CmsShortUrl extends BaseEntity {
    @Schema(description = "业务编码")
    private String shortUrlCode;
    @Schema(description = "短码")
    private String shortUrlKey;
    @Schema(description = "目标 path+query")
    private String shortUrlTarget;
    @Schema(description = "标题")
    private String shortUrlTitle;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "过期时间")
    private Date shortUrlExpireTime;
    @Schema(description = "状态")
    private ShortUrlStatusCodeEnum shortUrlStatusCode;
    @Schema(description = "点击次数")
    private Long shortUrlClickCount;
}
