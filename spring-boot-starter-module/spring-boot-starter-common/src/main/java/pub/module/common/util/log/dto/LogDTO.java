package pub.module.common.util.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LogDTO {
    @Schema(description = "日志编码")
    private String logCode;

    @Schema(description = "日志名称")
    private String logName;

    @Schema(description = "操作名称")
    private String logHandleName;

    @Schema(description = "方法名")
    private String logMethodName;

    @Schema(description = "日志内容")
    private String logContent;

    @Schema(description = "事务编码")
    private String logTransactionCode;

    @Schema(description = "用户名")
    private String logUserCode;

    @Schema(description = "客户端 IP")
    private String logClientIp;
}
