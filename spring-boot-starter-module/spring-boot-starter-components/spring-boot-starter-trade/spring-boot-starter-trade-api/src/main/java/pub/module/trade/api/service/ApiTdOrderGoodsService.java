package pub.module.trade.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pub.module.trade.api.dto.TdOrderGoodsDTO;

import java.util.List;

/**
 * 订单商品明细 Api Service
 *
 * @author tg
 * @since 2026-05-25
 */
public interface ApiTdOrderGoodsService {

    /**
     * 按商户（供应商）系统用户编码分页查询订单商品明细。
     *
     * @param tdGdSysUserCode 商户 system userCode（商品供应商编码）
     */
    IPage<TdOrderGoodsDTO> pageByTdGdSysUserCode(String tdGdSysUserCode, Integer pageNo, Integer pageSize);

    /**
     * 按商户（供应商）系统用户编码查询全部订单商品明细。
     */
    List<TdOrderGoodsDTO> listByTdGdSysUserCode(String tdGdSysUserCode);
}
