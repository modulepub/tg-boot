package pub.module.dating.crud.service;

import pub.module.dating.crud.entity.DtCustomerContactRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 联络记录 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
public interface DtCustomerContactRecordService extends IService<DtCustomerContactRecord> {
    DtCustomerContactRecord getByCode(String code);
}
