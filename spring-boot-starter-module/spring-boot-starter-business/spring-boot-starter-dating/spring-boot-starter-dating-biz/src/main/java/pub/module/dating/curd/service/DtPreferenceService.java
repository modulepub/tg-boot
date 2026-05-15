package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtPreference;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 偏好 Service
 *
 * @author tg
 * 2026-03-31 02:10:33
 */
public interface DtPreferenceService extends IService<DtPreference> {
    DtPreference getByCode(String code);
}
