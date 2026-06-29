package pub.module.system.api.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import pub.module.system.api.constants.SysUserCancellationProcessCodeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户账号注销申请")
public class SysUserCancellationApplyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键 id")
    private String id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "注销申请编码")
    private String cancellationCode;

    @Schema(description = "用户编码")
    private String userCode;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "用户昵称")
    private String userNickName;

    @Schema(description = "处理状态")
    private SysUserCancellationProcessCodeEnum cancellationProcessCode;

    @Schema(description = "处理人用户编码")
    private String processBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理时间")
    private LocalDateTime processAt;
}
