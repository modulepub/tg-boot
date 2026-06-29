package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import pub.module.common.model.po.BaseEntity;

/**
 * 微信小程序订阅消息模板，表 wx_ma_subscribe_template。
 */
@Data
@TableName("wx_ma_subscribe_template")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(title = "wx_ma_subscribe_template", description = "微信小程序订阅消息模板")
public class WxMaSubscribeTemplate extends BaseEntity {

    @Schema(description = "模板编码（业务主键，与业务侧常量一致）")
    private String wxMaSubscribeTemplateCode;

    @Schema(description = "微信订阅消息模板 ID")
    private String wxMaSubscribeTemplateId;

    @Schema(description = "模板说明（场景名、字段映射等）")
    private String wxMaSubscribeTemplateContent;
}
