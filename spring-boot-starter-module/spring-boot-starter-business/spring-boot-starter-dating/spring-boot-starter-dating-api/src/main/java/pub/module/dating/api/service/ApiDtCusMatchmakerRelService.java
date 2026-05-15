package pub.module.dating.api.service;

import pub.module.dating.api.service.dto.CusMkRelShowStatusUpdateDTO;

/**
 * Api 客户红娘关系 Service
 *
 * @author tg
 * 2026-03-25 00:36:20
 */
public interface ApiDtCusMatchmakerRelService {

    /**
     * 当前登录客户更新某条客户-红娘关系的「主页展示」状态（校验归属本人）。
     */
    void updateCusMkRelShowStatus(CusMkRelShowStatusUpdateDTO dto);

    /**
     * 将下单用户与商品供应商（红娘账号）建立客户-红娘关系。
     * 同一 {@code cusCode + mkCode} 仅保留一条，已存在则直接返回（幂等）。
     *
     * @param orderBuyerUserCode    下单用户 system userCode（客户绑定账号）
     * @param goodsSupplierUserCode 商品供应商 system userCode（红娘 mkUserCode）
     */
    void relateCustomerWithMatchmakerIfAbsent(String orderBuyerUserCode, String goodsSupplierUserCode);

    /**
     * 将当前登录用户对应的客户与指定红娘（按红娘编码）建立客户-红娘关系。
     * 同一 {@code cusCode + mkCode} 仅保留一条，已存在则直接返回（幂等）。
     * 用于用户确认发起牵线成功后，在「我的顾问」中可见该红娘。
     *
     * @param customerUserCode 客户绑定的 system userCode
     * @param mkCode           红娘编码 {@code dt_matchmaker.mk_code}
     */
    void relateCustomerWithMatchmakerByMkCodeIfAbsent(String customerUserCode, String mkCode);
}
