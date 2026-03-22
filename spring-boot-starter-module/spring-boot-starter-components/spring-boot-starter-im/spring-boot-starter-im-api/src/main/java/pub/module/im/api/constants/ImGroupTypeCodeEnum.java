package pub.module.im.api.constants;

import lombok.Getter;

@Getter
public enum ImGroupTypeCodeEnum {
    SR1("1", "客户服务1"),
    SR2("2", "客户服务2"),
    SR3("3", "客户服务3"),
    SR4("4", "客户服务4"),
    SR5("5", "客户服务5"),
    ;

    private final String code;
    private final String name;
    ImGroupTypeCodeEnum(String code, String name){
        this.code=code;
        this.name=name;
    }
}
