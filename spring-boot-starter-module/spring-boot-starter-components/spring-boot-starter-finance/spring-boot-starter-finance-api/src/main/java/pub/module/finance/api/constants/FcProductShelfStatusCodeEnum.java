package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcProductShelfStatusCodeEnum {
    UP("1", "上架"),
    DOWN("0", "下架"),
    ;
    private final String code;
    private final String text;

    FcProductShelfStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
