package pub.module.system.api.constants;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 推荐关系相关常量。
 *
 * <p>新用户注册时默认不保存推荐关系，仅当推荐人具备下列标签之一时才保存；
 * 例如对方不是红娘（不含 {@code matchmaker} 标签）时不保存推荐关系。</p>
 */
public final class UserReferenceRelationConstants {

    /** 红娘标签编码（与业务模块 MatchmakerTagConstants.MATCHMAKER_TAG_CODE 保持一致） */
    public static final String MATCHMAKER_TAG_CODE = "matchmaker";

    /** 红娘标签名称（用户标签表以名称存储） */
    public static final String MATCHMAKER_TAG_NAME = "红娘";

    /** 保存推荐关系所需的推荐人标签：key=标签编码, value=标签名称 */
    public static final Map<String, String> SAVE_REFERENCE_REQUIRED_TAGS;

    static {
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put(MATCHMAKER_TAG_CODE, MATCHMAKER_TAG_NAME);
        SAVE_REFERENCE_REQUIRED_TAGS = Collections.unmodifiableMap(tags);
    }

    private UserReferenceRelationConstants() {
    }

    /** 保存推荐关系所需的推荐人标签编码集合 */
    public static Set<String> requiredReferrerTagCodes() {
        return new LinkedHashSet<>(SAVE_REFERENCE_REQUIRED_TAGS.keySet());
    }

    /** 保存推荐关系所需的推荐人标签名称集合（用于按标签名称匹配） */
    public static Set<String> requiredReferrerTagNames() {
        return SAVE_REFERENCE_REQUIRED_TAGS.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
