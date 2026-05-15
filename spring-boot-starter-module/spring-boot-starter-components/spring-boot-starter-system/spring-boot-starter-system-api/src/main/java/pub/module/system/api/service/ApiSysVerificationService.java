package pub.module.system.api.service;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pub.module.system.api.vo.SysVerificationDTO;

import java.time.LocalDateTime;

/**
 * Api 验证码 Service
 *
 * @author tg
 * 2026-04-20 14:14:27
 */
public interface ApiSysVerificationService  {
    SysVerificationDTO set(@NotBlank(message = "verificationTypeCode not null") String verificationTypeCode,
                           @NotBlank(message = "verificationKey not null") String verificationKey,
                           @NotBlank(message = "verificationValue not null") String verificationValue,
                           @NotNull(message = "verificationExpireTime not null") LocalDateTime verificationExpireTime);
    SysVerificationDTO getByKey(@NotBlank(message = "verificationTypeCode not null") String verificationTypeCode,
                                @NotBlank(message = "verificationKey not null") String verificationKey);
    void delByKey(@NotBlank(message = "verificationTypeCode not null") String verificationTypeCode,
                  @NotBlank(message = "verificationKey not null") String verificationKey);

}
