package pub.module.system.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "绑定手机号结果")
public class BindPhoneResultVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否切换到了已有手机号账号")
    private boolean switchedAccount;

    @Schema(description = "切换账号时返回的新登录 token")
    private SysUserTokenVO token;
}
