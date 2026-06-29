package pub.module.wx.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "微信小程序文本内容安全检测响应")
public class WxMaMsgSecCheckResult {

    private boolean apiReachable;
    private Integer errCode;
    private String errMsg;
    private String suggest;
    private Integer label;
    private String traceId;
    private String rawSummary;
}
