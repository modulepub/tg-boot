package pub.module.system.api.constants;

import lombok.Getter;

@Getter
public enum PerTypeCodeEnum {
    ///** 类型   0：菜单   1：按钮   2：接口 */
    MENU("0", "菜单"),
    BUTTON("1", "按钮"),
    API("2", "接口"),
;

    PerTypeCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
    private final String code;

    private final String text;

}
