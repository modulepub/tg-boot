package pub.module.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pub.module.common.enums.BaseEnum;

/**
 * 统一业务异常（包含错误码）
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
@Getter
public class AuthException extends RuntimeException {
    private final BaseEnum errorCodeEnum;

    public AuthException(BaseEnum errorCodeEnum) {
        super(errorCodeEnum.getDesc());
        this.errorCodeEnum = errorCodeEnum;
    }
}