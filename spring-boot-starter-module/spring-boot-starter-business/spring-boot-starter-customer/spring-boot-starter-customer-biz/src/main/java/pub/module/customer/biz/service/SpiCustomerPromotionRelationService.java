package pub.module.customer.biz.service;

import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Spi 客户营销关系 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Service
public interface SpiCustomerPromotionRelationService {

    void assign(String promotionTaskTypeCode , List<String> cusCodeList,List<String> userCodeList);
    void dealt(String promotionTaskCode);
    void complete(String promotionTaskCode);

}
