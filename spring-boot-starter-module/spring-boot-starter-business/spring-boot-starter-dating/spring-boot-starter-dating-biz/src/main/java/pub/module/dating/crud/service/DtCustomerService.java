package pub.module.dating.crud.service;

import java.util.List;

import pub.module.dating.api.service.dto.CusCityResidenceOptionDTO;
import pub.module.dating.crud.entity.DtCustomer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客户 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
public interface DtCustomerService extends IService<DtCustomer> {
    DtCustomer getByCode(String code);

    /** 客户表中已出现的生活城市（按编码分组，含名称） */
    List<CusCityResidenceOptionDTO> listDistinctResidenceCities();
}
