package pub.module.distribution.api.service;

/**
 * 按业务线解析推广人角色（普通/红娘等）。
 */
public interface SpiDistPromoterRoleResolver {

    boolean supports(String distBizLineCode);

    /**
     * @return {@link pub.module.distribution.api.constants.DistPromoterRoleCodeEnum} 的 code
     */
    String resolvePromoterRoleCode(String beneficiaryUserCode);
}
