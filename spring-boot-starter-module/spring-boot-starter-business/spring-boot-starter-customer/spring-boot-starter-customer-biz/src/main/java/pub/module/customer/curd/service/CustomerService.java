package pub.module.customer.curd.service;

import pub.module.customer.curd.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客户 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
public interface CustomerService extends IService<Customer> {
    Customer getByCode(String code);
}
