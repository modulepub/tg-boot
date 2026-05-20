package pub.module.dating.biz.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.curd.entity.DtPreference;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 偏好列表展示字段回填（从客户表联查姓名、头像、年龄等）。
 */
@Service
public class DtPreferenceDisplayService {

    @Resource
    private ApiCustomerService apiCustomerService;

    /**
     * @param fillTarget true：回填被喜欢嘉宾 {@code preferenceTargetCus*}；false：回填发起方 {@code preferenceCus*}
     */
    public void enrichPeerDisplay(IPage<DtPreference> page, boolean fillTarget) {
        if (page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
            return;
        }
        List<String> peerCodes = page.getRecords().stream()
                .map(row -> fillTarget ? row.getPreferenceTargetCusCode() : row.getPreferenceCusCode())
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (peerCodes.isEmpty()) {
            return;
        }
        Map<String, CustomerDTO> cusMap = apiCustomerService.listByCusCodes(peerCodes).stream()
                .filter(c -> StrUtil.isNotBlank(c.getCusCode()))
                .collect(Collectors.toMap(CustomerDTO::getCusCode, Function.identity(), (a, b) -> a));
        for (DtPreference row : page.getRecords()) {
            String peerCode = fillTarget ? row.getPreferenceTargetCusCode() : row.getPreferenceCusCode();
            CustomerDTO cus = cusMap.get(peerCode);
            if (fillTarget) {
                fillTargetFromCustomer(row, cus);
            }
            else {
                fillCusFromCustomer(row, cus);
            }
        }
    }

    public static void fillTargetFromCustomer(DtPreference pref, CustomerDTO cus) {
        if (pref == null || cus == null) {
            return;
        }
        pref.setPreferenceTargetCusName(resolveDisplayName(cus));
        pref.setPreferenceTargetCusAge(toIntegerAge(cus.getCusAge()));
        pref.setPreferenceTargetCusAvatar(cus.getCusAvatar());
        pref.setPreferenceTargetCusCityResidenceCode(cus.getCusCityResidenceCode());
        pref.setPreferenceTargetCusCityResidenceName(cus.getCusCityResidenceName());
    }

    public static void fillCusFromCustomer(DtPreference pref, CustomerDTO cus) {
        if (pref == null || cus == null) {
            return;
        }
        pref.setPreferenceCusName(resolveDisplayName(cus));
        pref.setPreferenceCusAge(toIntegerAge(cus.getCusAge()));
        pref.setPreferenceCusAvatar(cus.getCusAvatar());
        pref.setPreferenceCusCityResidenceCode(cus.getCusCityResidenceCode());
        pref.setPreferenceCusCityResidenceName(cus.getCusCityResidenceName());
    }

    private static String resolveDisplayName(CustomerDTO cus) {
        if (StrUtil.isNotBlank(cus.getCusName())) {
            return cus.getCusName().trim();
        }
        if (StrUtil.isNotBlank(cus.getCusNickName())) {
            return cus.getCusNickName().trim();
        }
        return null;
    }

    private static Integer toIntegerAge(Long age) {
        if (age == null) {
            return null;
        }
        return age.intValue();
    }
}
