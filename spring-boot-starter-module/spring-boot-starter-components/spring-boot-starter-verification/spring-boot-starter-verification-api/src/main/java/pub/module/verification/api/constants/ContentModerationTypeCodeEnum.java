package pub.module.verification.api.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 待检测内容类型
 */
@Getter
@RequiredArgsConstructor
public enum ContentModerationTypeCodeEnum {

    TEXT("TEXT"),
    IMAGE("IMAGE"),
    VIDEO("VIDEO");

    private final String code;
}
