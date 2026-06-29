package pub.module.distribution.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pub.module.distribution.api.service.dto.DistEnterpriseBillStatsDTO;
import pub.module.distribution.api.service.dto.DistStaffSettleCustomerDTO;
import pub.module.distribution.api.service.dto.DistUserBillPromoteStatsDTO;
import pub.module.distribution.api.service.dto.DistUserBillSettleRecordDTO;
import pub.module.distribution.api.service.dto.DistUserBillSummaryDTO;

import java.util.List;
import pub.module.system.api.service.dto.UserDTO;

/**
 * 用户账单汇总 API。
 */
public interface ApiDistUserBillSummaryService {

    /**
     * 查询用户账单汇总；无记录时返回各金额为 0 的默认对象。
     */
    DistUserBillSummaryDTO getSummary(String distUserCode, String distBizLineCode);

    /**
     * 用户注册后初始化账单汇总（幂等）。
     */
    void initOnUserRegistered(UserDTO user, String distBizLineCode);

    /**
     * 推广页顶部统计：按推广人（当前用户）汇总直推下级账单。
     */
    DistUserBillPromoteStatsDTO getPromoteStats(String distInviterUserCode, String distBizLineCode);

    /**
     * 推广明细分页：查询邀请人编码为推广人的账单汇总行。
     */
    IPage<DistUserBillSummaryDTO> pageByPromoter(String distInviterUserCode, String distBizLineCode,
            int pageNo, int pageSize);

    /**
     * 按用户编码集合聚合账单汇总（企业业绩顶部统计）。
     */
    DistEnterpriseBillStatsDTO getStatsByUserCodes(List<String> distUserCodes, String distBizLineCode);

    /**
     * 按用户编码集合分页查询账单汇总（企业业绩明细）。
     */
    IPage<DistUserBillSummaryDTO> pageByUserCodes(List<String> distUserCodes, String distBizLineCode,
            int pageNo, int pageSize);

    /**
     * 服务期到期批量结算账单记录，回写已入账字段。
     *
     * @return 本次结算笔数
     */
    int settleDueBillRecords();

    /**
     * 推广人查询下级用户的账单结算明细（需校验邀请关系）。
     */
    IPage<DistUserBillSettleRecordDTO> pageSettleRecordsByInvitee(String distInviterUserCode, String distPayerUserCode,
            String distBizLineCode, int pageNo, int pageSize);

    /**
     * 按付款用户分页查询账单结算明细（不含邀请关系校验，由调用方鉴权）。
     */
    IPage<DistUserBillSettleRecordDTO> pageSettleRecordsByPayer(String distPayerUserCode, String distBizLineCode,
            int pageNo, int pageSize);

    /**
     * 解析企业结算纳入的付款用户编码（红娘本人 + 直推下级）。
     */
    List<String> resolveSettlePayerUserCodes(List<String> distMatchmakerUserCodes, String distBizLineCode);

    /**
     * 解析单个红娘结算纳入的付款用户编码（仅直推下级客户）。
     */
    List<String> resolveStaffSettlePayerUserCodes(String distMatchmakerUserCode, String distBizLineCode);

    /**
     * 红娘名下客户未结算佣金明细分页。
     */
    IPage<DistStaffSettleCustomerDTO> pageStaffSettleCustomers(String distMatchmakerUserCode, String distBizLineCode,
            int pageNo, int pageSize);

    /**
     * 汇总红娘名下客户未结算佣金总额。
     */
    java.math.BigDecimal sumStaffUnsettledCommission(String distMatchmakerUserCode, String distBizLineCode);
}
