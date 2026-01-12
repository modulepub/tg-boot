package pub.module.finance.curd.service;

import pub.module.finance.curd.entity.FcProduct;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 产品管理
 * @author tg
 * @since 2025-10-11
 * @version V1.0
 */
public interface IFcProductService extends IService<FcProduct> {
    FcProduct getByCode(String code);
}
