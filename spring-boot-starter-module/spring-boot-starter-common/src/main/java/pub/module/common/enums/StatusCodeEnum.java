package pub.module.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/**
 * 统一布尔式状态机：库表与 API 中<strong>仅存字符串 {@code "0"} / {@code "1"}</strong>。
 * <p>
 * 枚举常量名 {@link #NO}、{@link #YES} 仅为 Java 标识，<strong>不是</strong>持久化取值；
 * 写入与比较请使用 {@link #getCode()}（即 {@code "0"} / {@code "1"}），
 * 禁止向库表或接口传 {@code YES}、{@code NO} 字面量。
 * <p>
 * 库表 {@code null} 或无法识别的编码在业务上视同否；多步流程中间态请使用 {@code *ProcessCode} 字段。
 * 约定详见仓库根 {@code README.md}「布尔式状态机」与 {@code AGENTS.md}。
 */
@Getter
public enum StatusCodeEnum implements BaseEnum {
    /** 否；持久化编码 {@code "0"} */
    NO("0", "否"),
    /** 是；持久化编码 {@code "1"} */
    YES("1", "是"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    StatusCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static StatusCodeEnum fromJson(Object raw) {
        return parse(raw);
    }

    /** 解析 0/1；null、空串及非 0/1 编码返回 null（视同否）。 */
    public static StatusCodeEnum parse(Object raw) {
        if (raw instanceof StatusCodeEnum e) {
            return e;
        }
        if (raw == null) {
            return null;
        }
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) {
            return null;
        }
        if (YES.code.equals(s)) {
            return YES;
        }
        if (NO.code.equals(s)) {
            return NO;
        }
        return null;
    }

    /** 是否为「是」；null 视同否。 */
    public static boolean isYesValue(StatusCodeEnum status) {
        return status == YES;
    }
}
