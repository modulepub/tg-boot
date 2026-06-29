package pub.module.verification.api.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import pub.module.common.enums.BaseEnum;

/**
 * 资产认证申请流程（{@code assetCertProcessCode}）。
 */
@Getter
public enum VtAssetCertProcessCodeEnum implements BaseEnum {

    DRAFT("0", "待提交"),
    REVIEWING("1", "审核中"),
    APPROVED("2", "审核通过"),
    REJECTED("3", "审核拒绝"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    VtAssetCertProcessCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static VtAssetCertProcessCodeEnum fromJson(Object raw) {
        return parse(raw);
    }

    public static VtAssetCertProcessCodeEnum parse(Object raw) {
        return BaseEnum.parse(raw, VtAssetCertProcessCodeEnum.class);
    }

    public static VtAssetCertProcessCodeEnum effective(VtAssetCertProcessCodeEnum process) {
        return process != null ? process : DRAFT;
    }

    public static boolean canSubmit(VtAssetCertProcessCodeEnum process) {
        VtAssetCertProcessCodeEnum p = effective(process);
        return p == DRAFT || p == REJECTED;
    }
}
