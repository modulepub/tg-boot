package pub.module.customer.api.service;


import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.system.api.service.dto.UserDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Api 客户 Service
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
public interface ApiCustomerService  {

    void importData(Map<String,Object> data);

    /**
     * 按登录用户确保存在客户档案：若无则创建最小客户记录（幂等）。
     */
    void initCustomerByUser(UserDTO user);

    CustomerDTO getCusByUserCode(String userCode);
    List<CustomerDTO> listAll(List<String> notIn);
    CustomerDTO getCusByCusCode(String cusCode);

    /** 批量按客户编码查询（联系人列表等回填展示字段） */
    List<CustomerDTO> listByCusCodes(Collection<String> cusCodes);

    /** 批量按系统用户编码查询（联系人申请列表回填申请人信息） */
    List<CustomerDTO> listByUserCodes(Collection<String> userCodes);

    /**
     * 用户端：按当前用户绑定客户更新字段；{@code patch} 中出现且可写的属性才会写入（前端传什么更新什么）。
     * 若传入 {@code cusCode} 则须与当前客户一致。
     */
    CustomerDTO updateCurrCustomerPartial(String userCode, Map<String, Object> patch);

    /**
     * 为用户累加会员权益（加好友 / 推荐 / 牵线次数）。用于支付成功回调、运营发放等可信链路；
     * 用户端 {@link #updateCurrCustomerPartial} 不允许直接改写这些字段。
     *
     * @param tdOdGdCode            订单商品明细编码；非空时写入充值记录表并按此幂等，已存在相同明细编码则不再充值
     * @param tdOdCode              订单编码，可空，仅写入记录便于查询
     * @param userCode              系统用户编码
     * @param addFriendRightDelta   添加好友权益增量，{@code null} 表示不调整该字段
     * @param recommendRightDelta   推荐权益增量，{@code null} 表示不调整该字段
     * @param matchRightDelta       牵线权益增量，{@code null} 表示不调整该字段
     */
    void rechargeMemberBenefits(String tdOdGdCode, String tdOdCode, String userCode, Long addFriendRightDelta, Long recommendRightDelta, Long matchRightDelta);

}
