package pub.module.trade.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.trade.crud.entity.TdGoodsBenefit;

import java.util.List;

/**
 * 商品权益服务
 */
public interface ITdGoodsBenefitService extends IService<TdGoodsBenefit> {

    List<TdGoodsBenefit> listByTdGdCode(String tdGdCode);

    void replaceByTdGdCode(String tdGdCode, List<TdGoodsBenefit> benefitList);

    void removeByTdGdCode(String tdGdCode);
}
