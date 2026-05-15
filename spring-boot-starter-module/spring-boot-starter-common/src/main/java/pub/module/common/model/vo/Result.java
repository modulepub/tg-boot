package pub.module.common.model.vo;

import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;


/**
 * 统一返回结果类
 * 封装API响应数据
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    public enum CodeEnum {
		OK("0", "成功"),
		AUTH_FAIL("1", "验证失败！"),
		ANY_FAIL("2", "任何失败！"),
        ;
        private final String code;
        private final String message;

        CodeEnum(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    /**
     * 返回处理消息
     */
    private String message = "操作成功";



    /**
     * 返回代码
     */
    private String code;

    /**
     * 返回数据对象 data
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    public Result() {
    }

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setCode(CodeEnum.OK.code);
        return r;
    }

    /**
     * api 返回响应数据
     * @param msg 消息
     * @param data 数据
     */
    public static <T> Result<T> ok(String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(CodeEnum.OK.code);
        r.setMessage(msg);
        r.setData(data);
        return r;
    }

    /**
     * api 返回响应数据
     * @param data 数据
     */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(CodeEnum.OK.code);
        r.setData(data);
        return r;
    }

    /**
     * api 返回错误信息
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(CodeEnum.ANY_FAIL.code);
        r.setMessage(message);
        return r;
    }

    /**
     * api 返回错误信息
     * @param code 错误码
     * @param msg 错误消息
     */
    public static <T> Result<T> error(String code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(msg);
        return r;
    }
}