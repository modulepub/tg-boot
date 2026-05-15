package pub.module.common.exception;

import lombok.Getter;
import pub.module.common.constants.ErrorCodeEnum;

/**
 * 统一业务异常（包含错误码）
 */
@Getter
public class BizException extends RuntimeException {
    private final ErrorCodeEnum errorCodeEnum;

    public BizException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getDesc());
        this.errorCodeEnum = errorCodeEnum;
    }
}