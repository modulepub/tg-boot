package pub.module.customer.curd.service;

import java.util.List;

import pub.module.customer.api.service.dto.CusCityResidenceOptionDTO;
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

    /** 客户表中已出现的生活城市（按编码分组，含名称） */
    List<CusCityResidenceOptionDTO> listDistinctResidenceCities();
}
