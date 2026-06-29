package pub.module.common.exception;

import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 统一业务异常（包含错误码）
 */
@Getter
public class BizException extends RuntimeException {
    private final BaseEnum errorCodeEnum;

    public BizException(BaseEnum errorCodeEnum) {
        super(errorCodeEnum.getDesc());
        this.errorCodeEnum = errorCodeEnum;
    }
}