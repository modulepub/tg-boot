package pub.module.cms.api.constants;

import lombok.Getter;

@Getter
public enum NodeTypeCodeEnum {
    CATALOG("catalog", "栏目"),
    DOCUMENT("document", "文章"),
    ;
    private final String code;
    private final String message;

    NodeTypeCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}