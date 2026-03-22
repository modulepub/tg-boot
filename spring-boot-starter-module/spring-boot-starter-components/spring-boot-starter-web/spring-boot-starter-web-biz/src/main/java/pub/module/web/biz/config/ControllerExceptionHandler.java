package pub.module.web.biz.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pub.module.web.vo.Result;

/**
 * 控制器异常处理器
 * 全局处理控制器抛出的异常
 *
 * @author PZ
 * @version V1.0
 * @since 2026-01-02
 */
@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    //正常业务异常处理
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error("服务器错误，管理员已收到反馈");
    }

    //未知异常处理
    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> runtimeExceptionHandler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Result.error("攻城狮已收到异常，火速排查中");
    }

    //未知异常处理
    @ExceptionHandler({IllegalArgumentException.class})
    public Result<String> illegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return Result.error(e.getMessage());
    }

}
