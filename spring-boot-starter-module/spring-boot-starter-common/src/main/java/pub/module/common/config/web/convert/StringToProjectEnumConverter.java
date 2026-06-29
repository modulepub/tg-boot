package pub.module.common.config.web.convert;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;
import pub.module.common.enums.BaseEnum;

import java.util.Set;

/**
 * GET 查询参数等场景：将字典 code（如 {@code "0"}、{@code "idCard"}）转为项目枚举。
 * <p>
 * 使用 {@link ConditionalGenericConverter}，优先于 Spring 默认按枚举常量名转换的工厂。
 */
public class StringToProjectEnumConverter implements ConditionalGenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(String.class, Enum.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> target = targetType.getObjectType();
        return Enum.class.isAssignableFrom(target) && BaseEnum.class.isAssignableFrom(target);
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        String text = source.toString().trim();
        if (text.isEmpty()) {
            return null;
        }
        return parseBaseEnum(text, targetType.getObjectType());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & BaseEnum> T parseBaseEnum(String text, Class<?> enumType) {
        return BaseEnum.parse(text, (Class<T>) enumType);
    }
}
