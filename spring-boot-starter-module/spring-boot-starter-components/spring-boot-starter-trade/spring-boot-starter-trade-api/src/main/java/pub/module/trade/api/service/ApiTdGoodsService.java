package pub.module.trade.api.service;

import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.dto.TdGoodsBenefitDTO;
import pub.module.trade.api.dto.TdGoodsMemberBenefitDeltaDTO;

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

    /**
     * 按商品编码判断是否存在。
     */
    boolean existsByTdGdCode(String tdGdCode);

    /**
     * 按供货商编码与商品类目编码查询单条商品。
     */
    TdGoodsDTO getByTdGdSysUserCodeAndCgyCode(String tdGdSysUserCode, String tdGdCgyCode);

    /**
     * 按主键更新商品（需包含 {@code id}）。
     */
    void updateGoods(TdGoodsDTO dto);

    /**
     * 逻辑删除全部测试商品（{@code td_gd_test_status_code = 1}）。
     *
     * @return 删除条数
     */
    int removeTestGoods();

    /**
     * 按商品编码查询权益列表，按 {@code seqNo} 升序。
     */
    List<TdGoodsBenefitDTO> listBenefitsByTdGdCode(String tdGdCode);

    /**
     * 解析会员三类权益增量（未配置 key 时默认 10）。
     */
    TdGoodsMemberBenefitDeltaDTO resolveMemberBenefitDelta(String tdGdCode);
}
