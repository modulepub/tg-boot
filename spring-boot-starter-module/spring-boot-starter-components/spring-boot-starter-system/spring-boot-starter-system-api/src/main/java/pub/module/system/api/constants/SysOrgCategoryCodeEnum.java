package pub.module.system.api.constants;

import lombok.Getter;

@Getter
public enum SysOrgCategoryCodeEnum {
    COM("com", "公司"),
    DEPT("dept", "部门"),
;

    SysOrgCategoryCodeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }
    private final String code;

    private final String text;

}
