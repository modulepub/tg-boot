package pub.module.plugins.dating.distribution;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.constants.DistPromoterRoleCodeEnum;
import pub.module.distribution.api.service.SpiDistPromoterRoleResolver;

@Component
public class DatingDistPromoterRoleResolver implements SpiDistPromoterRoleResolver {

    @Resource
    private ApiDtMatchmakerService apiDtMatchmakerService;

    @Override
    public boolean supports(String distBizLineCode) {
        return DistBizLineCodeEnum.DATING.getCode().equals(distBizLineCode);
    }

    @Override
    public String resolvePromoterRoleCode(String beneficiaryUserCode) {
        return apiDtMatchmakerService.isMatchmakerByUserCode(beneficiaryUserCode)
                ? DistPromoterRoleCodeEnum.MATCHMAKER.getCode()
                : DistPromoterRoleCodeEnum.NORMAL.getCode();
    }
}
