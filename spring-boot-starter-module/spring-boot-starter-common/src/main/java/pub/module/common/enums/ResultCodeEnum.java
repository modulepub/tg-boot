package pub.module.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;


@Getter
public enum  ResultCodeEnum implements BaseEnum {
    OK("0", "成功"),
    AUTH_FAIL("1", "验证失败！"),
    //通用错误，全局 toast 展示 message
    ANY_FAIL("2", "通用错误！"),
    ;

    @EnumValue
    public final String code;
    public final String desc;

    ResultCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
