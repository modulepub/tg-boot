package pub.module.finance.api.constants;

import lombok.Getter;

@Getter
public enum FcLoanTypeCodeEnum {
    CREDIT("1", "授信"),
    US_CREDIT("2", "用信"),
    ;
    private final String code;
    private final String text;

    FcLoanTypeCodeEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    // 核心方法：根据 code 获取枚举对象
    public static String getTextByCode(String code) {
        // 遍历所有枚举实例
        for (FcLoanTypeCodeEnum status : FcLoanTypeCodeEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.getText(); // 匹配到则返回
            }
        }
        // 未匹配到的处理：返回 null 或抛出异常
        return "";
    }
}
