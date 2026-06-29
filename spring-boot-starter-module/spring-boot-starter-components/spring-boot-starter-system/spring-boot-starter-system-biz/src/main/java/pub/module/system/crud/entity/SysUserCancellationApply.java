package pub.module.system.crud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;
import pub.module.system.api.constants.SysUserCancellationProcessCodeEnum;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户账号注销申请")
public class SysUserCancellationApply extends BaseEntity {

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理时间")
    private LocalDateTime processAt;
}
