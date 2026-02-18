package pub.module.customer.biz.service;

import org.springframework.stereotype.Service;
import pub.module.customer.curd.entity.CustomerContactRecord;


/**
 * Spi 联络记录 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Service
public interface SpiCustomerContactRecordService {
    void doRecord(CustomerContactRecord customerContactRecord);
}
