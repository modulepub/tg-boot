package pub.module.im.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import pub.module.im.api.constants.ImNoticePublishStateCodeEnum;
import pub.module.im.api.constants.ImNoticeTargetTypeCodeEnum;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_notice")
@Schema(description = "IM-全员通知")
public class ImNotice extends BaseEntity {

    @Schema(description = "通知编码")
    private String imNoticeCode;

    @Schema(description = "通知标题")
    private String imNoticeName;

    @Schema(description = "通知正文")
    private String imNoticeText;

    @Schema(description = "通知图片")
    private String imNoticeImg;

    @Schema(description = "跳转链接")
    private String imNoticeUrl;

    @Schema(description = "发送人IM用户编码")
    private String imNoticeSenderUserCode;

    @Schema(description = "发布状态")
    private ImNoticePublishStateCodeEnum imNoticePublishStateCode;

    @Schema(description = "通知对象类型")
    private ImNoticeTargetTypeCodeEnum imNoticeTargetTypeCode;

    @Schema(description = "发送成功数")
    private Integer imNoticeSendCount;

    @Schema(description = "发送失败数")
    private Integer imNoticeFailCount;
}
