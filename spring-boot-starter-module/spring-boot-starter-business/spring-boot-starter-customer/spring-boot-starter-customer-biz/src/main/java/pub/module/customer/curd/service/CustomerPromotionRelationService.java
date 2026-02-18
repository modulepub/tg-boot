package pub.module.customer.curd.service;

import pub.module.customer.curd.entity.CustomerPromotionRelation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客户营销关系 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
public interface CustomerPromotionRelationService extends IService<CustomerPromotionRelation> {
    CustomerPromotionRelation getByCode(String code);
}
