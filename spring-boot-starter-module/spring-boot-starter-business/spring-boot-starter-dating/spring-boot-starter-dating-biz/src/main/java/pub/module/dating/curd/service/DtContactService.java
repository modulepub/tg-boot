package pub.module.dating.curd.service;

import pub.module.dating.curd.entity.DtContact;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 联系人 Service
 *
 * @author tg
 * 2026-05-01 23:01:09
 */
public interface DtContactService extends IService<DtContact> {
    DtContact getByCode(String code);
}
