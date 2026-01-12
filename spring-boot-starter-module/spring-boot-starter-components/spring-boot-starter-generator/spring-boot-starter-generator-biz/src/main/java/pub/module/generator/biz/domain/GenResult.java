package pub.module.generator.biz.domain;

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
public class GenResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    public enum CodeEnum {
		OK(200, "成功"),
		AUTH_FAIL(500, "验证失败！"),
		ANY_FAIL(500, "任何失败！"),
        ;
        private final Integer code;
        private final String message;

        CodeEnum(Integer code, String message) {
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
    private Integer code;

    /**
     * 返回数据对象 data
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();

    public GenResult() {
    }

    public static <T> GenResult<T> ok() {
        GenResult<T> r = new GenResult<>();
        r.setCode(CodeEnum.OK.code);
        return r;
    }

    /**
     * api 返回响应数据
     * @param msg 消息
     * @param data 数据
     */
    public static <T> GenResult<T> ok(String msg, T data) {
        GenResult<T> r = new GenResult<>();
        r.setCode(CodeEnum.OK.code);
        r.setMessage(msg);
        r.setData(data);
        return r;
    }

    /**
     * api 返回响应数据
     * @param data 数据
     */
    public static <T> GenResult<T> ok(T data) {
        GenResult<T> r = new GenResult<>();
        r.setCode(CodeEnum.OK.code);
        r.setData(data);
        return r;
    }

    /**
     * api 返回错误信息
     * @param message 错误消息
     */
    public static <T> GenResult<T> error(String message) {
        GenResult<T> r = new GenResult<>();
        r.setCode(CodeEnum.ANY_FAIL.code);
        r.setMessage(message);
        return r;
    }

    /**
     * api 返回错误信息
     * @param code 错误码
     * @param msg 错误消息
     */
    public static <T> GenResult<T> error(int code, String msg) {
        GenResult<T> r = new GenResult<>();
        r.setCode(code);
        r.setMessage(msg);
        return r;
    }
}