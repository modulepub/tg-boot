package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "微信小程序多媒体内容安全异步检测响应")
public class WxMaMediaCheckAsyncResult {

    private boolean apiReachable;
    private Integer errCode;
    private String errMsg;
    private String traceId;
    private String rawSummary;
}
