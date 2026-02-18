package pub.module.customer.curd.service;

import pub.module.customer.curd.entity.CustomerContactRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 联络记录 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
public interface CustomerContactRecordService extends IService<CustomerContactRecord> {
    CustomerContactRecord getByCode(String code);
}
