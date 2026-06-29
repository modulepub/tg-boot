package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订阅消息模板字段提示（从模板说明解析，供管理端测试发送填参）。
 */
@Data
public class WxMaSubscribeTemplateFieldDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "微信模板字段 key，如 thing2、date1")
    private String fieldKey;

    @Schema(description = "字段说明")
    private String fieldLabel;
}
