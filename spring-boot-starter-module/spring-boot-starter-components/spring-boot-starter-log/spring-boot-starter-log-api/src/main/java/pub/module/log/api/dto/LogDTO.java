package pub.module.log.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LogDTO {
    /**
     * 日志编码
     */
    @Schema(description = "日志编码")
    private String logCode;

    /**
     * 日志名称
     */
    @Schema(description = "日志名称")
    private String logName;

    @Schema(description = "操作名称")
    private String logHandleName;

    /**
     * 方法名
     */
    @Schema(description = "方法名")
    private String logMethodName;

    /**
     * 日志内容
     */
    @Schema(description = "日志内容")
    private String logContent;

    /**
     * 事务编码
     */
    @Schema(description = "事务编码")
    private String logTransactionCode;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String logUserCode;

    /**
     * 客户端
     */
    @Schema(description = "客户端 IP")
    private String logClientIp;
}
