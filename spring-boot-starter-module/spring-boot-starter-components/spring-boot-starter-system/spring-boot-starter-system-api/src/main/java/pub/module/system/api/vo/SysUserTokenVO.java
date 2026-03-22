package pub.module.system.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "SysUserTokenVO")
public class SysUserTokenVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "accessToken")
    private String accessToken;

    @Schema(description = "refreshToken")
    private String refreshToken;

    @Schema(description = "accessToken 过期时间")
    private Long accessTokenExpire;

    @Schema(description = "refreshToken 过期时间")
    private Long refreshTokenExpire;
}
