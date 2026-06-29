package pub.module.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 项目统一字典枚举规范：持久化字段多为 varchar，编码以字符串形式存储。
 * <p>
 * 支持数字类 code（如 {@code "0"}、{@code "1"}）与语义类 code（如 {@code "idCard"}、{@code "contact"}）。
 * 具体枚举需实现本接口，并提供 {@code @JsonCreator} 工厂方法委托 {@link #parse(Object, Class)}。
 */
public interface BaseEnum {

    /** 统一枚举编码（入库、接口），与库中字典项值一致 */
    String getCode();

    /** 中文或默认描述；国际化时可改为消息 key */
    String getDesc();

    @JsonValue
    default String toJson() {
        return getCode();
    }

    static <T extends Enum<T> & BaseEnum> T of(String code, Class<T> enumClass) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (T item : enumClass.getEnumConstants()) {
            if (code.equals(item.getCode())) {
                return item;
            }
        }
        throw new IllegalArgumentException("无效枚举 " + code);
    }

    /**
     * 兼容 JSON 数字或字符串数字（如 {@code 1}、{@code "1"}）。
     */
    static <T extends Enum<T> & BaseEnum> T parse(Object raw, Class<T> enumClass) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof String s) {
            if (s.isBlank()) {
                return null;
            }
            return of(s.trim(), enumClass);
        }
        if (raw instanceof Number n) {
            return of(String.valueOf(n.longValue()), enumClass);
        }
        return of(String.valueOf(raw).trim(), enumClass);
    }
}
