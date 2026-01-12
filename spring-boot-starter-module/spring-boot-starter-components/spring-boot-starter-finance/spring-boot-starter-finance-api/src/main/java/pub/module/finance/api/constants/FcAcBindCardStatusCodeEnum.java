package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcAcBindCardStatusCodeEnum {
    NOT("0", "未绑卡"),
    YES("1", "绑卡成功"),
    ;
    private final String code;
    private final String text;

    FcAcBindCardStatusCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }
}
