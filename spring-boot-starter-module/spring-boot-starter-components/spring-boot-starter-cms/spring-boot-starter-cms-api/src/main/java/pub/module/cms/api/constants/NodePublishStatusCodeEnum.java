package pub.module.cms.api.constants;

import lombok.Getter;

@Getter
public enum NodePublishStatusCodeEnum {
    YES("1", "已发布"),
    NO("0", "未发布"),
    ;
    private final String code;
    private final String message;

    NodePublishStatusCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}