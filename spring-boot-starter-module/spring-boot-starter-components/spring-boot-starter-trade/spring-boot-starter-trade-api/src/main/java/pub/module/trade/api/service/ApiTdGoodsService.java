package pub.module.trade.api.service;

import pub.module.trade.api.dto.TdGoodsDTO;

import java.util.List;

/**
 * 商品业务 API（由 trade-biz 实现）
 *
 * @author tg
 * @since 2026-05-04
 */
public interface ApiTdGoodsService {

    /**
     * 新增商品
     *
     * @param dto 商品入参
     * @return 商品编码 {@code tdGdCode}（入参未填编码时由系统生成）
     */
    String addGoods(TdGoodsDTO dto);

    /**
     * 按供货商编码查询商品列表，按 {@code seqNo} 升序。
     *
     * @param tdGdSysUserCode 供货商（供应商）编码
     */
    List<TdGoodsDTO> listByTdGdSysUserCode(String tdGdSysUserCode);
}
