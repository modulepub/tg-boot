package pub.module.dating.biz.service;

import org.springframework.stereotype.Service;
import pub.module.dating.crud.entity.DtCustomerContactRecord;


/**
 * Spi 联络记录 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Service
public interface SpiDtCustomerContactRecordService {
    void doRecord(DtCustomerContactRecord customerContactRecord);
}
