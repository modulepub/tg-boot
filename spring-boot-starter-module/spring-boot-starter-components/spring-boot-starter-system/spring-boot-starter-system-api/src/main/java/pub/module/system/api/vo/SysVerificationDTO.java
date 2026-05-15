package pub.module.system.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pub.module.common.model.po.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 验证码 对象
 *
 * @author tg
 * 2026-04-20 14:14:27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "验证码")
public class SysVerificationDTO extends BaseEntity {
    /**
     * 编码
     */
    @Schema(description = "编码")
    private String verificationCode;

    /**
     * 验证码值
     */
    @Schema(description = "验证码KEY")
    private String verificationKey;
    /**
     * 验证码值
     */
    @Schema(description = "验证码值")
    private String verificationValue;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "过期时间")
    private LocalDateTime verificationExpireTime;

    /**
     * 验证类型
     */
    @Schema(description = "验证类型")
    private String verificationTypeCode;


}
