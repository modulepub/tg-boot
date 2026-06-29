package pub.module.wx.crud.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.po.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wx_ma_subscribe_send_log")
@Schema(description = "微信小程序订阅消息发送日志")
public class WxMaSubscribeSendLog extends BaseEntity {

    @Schema(description = "业务编码")
    private String sendLogCode;

    @Schema(description = "幂等键")
    private String idempotentKey;

    @Schema(description = "接收人 openId")
    private String toOpenId;

    @Schema(description = "模板 ID")
    private String templateId;

    @Schema(description = "跳转小程序页面")
    private String jumpPage;

    @Schema(description = "发送内容 JSON")
    private String sendDataJson;

    @Schema(description = "发送状态")
    private StatusCodeEnum sendStatusCode;

    @Schema(description = "微信错误码")
    private String wxErrCode;

    @Schema(description = "微信错误信息")
    private String wxErrMsg;
}
