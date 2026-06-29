package pub.module.dating.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pub.module.dating.api.service.dto.EnterpriseStaffDTO;
import pub.module.dating.api.service.dto.MatchmakingCompanyApplyDTO;
import pub.module.dating.api.service.dto.MatchmakingCompanyApplySubmitVO;
import pub.module.distribution.api.service.dto.DistEnterpriseBillStatsDTO;
import pub.module.distribution.api.service.dto.DistSettleBatchDTO;
import pub.module.distribution.api.service.dto.DistStaffSettleCustomerDTO;
import pub.module.distribution.api.service.dto.DistUserBillSettleRecordDTO;
import pub.module.distribution.api.service.dto.DistUserBillSummaryDTO;

/**
 * Api 婚介公司 Service
 */
public interface ApiDtMatchmakingCompanyService {

    /** 当前登录用户作为管理员查询其企业入驻信息 */
    MatchmakingCompanyApplyDTO getMyCompanyAsAdmin(String adminUserCode);

    /** 提交/更新企业入驻申请 */
    MatchmakingCompanyApplyDTO submitApply(String adminUserCode, MatchmakingCompanyApplySubmitVO vo);

    /** 企业管理员确认已完成对公认证转账 */
    MatchmakingCompanyApplyDTO confirmTransfer(String adminUserCode);

    /**
     * 企业管理员分页查询旗下红娘。
     *
     * @param auditTab {@code approved} 已审核；{@code pending} 未审核
     */
    IPage<EnterpriseStaffDTO> listStaff(String adminUserCode, String auditTab, Integer pageNo, Integer pageSize);

    /** 企业管理员查询旗下红娘详情（审核用） */
    EnterpriseStaffDTO getStaffDetail(String adminUserCode, String id);

    /** 企业管理员审核通过旗下红娘（需上传附件，进入平台审核） */
    void approveStaff(String adminUserCode, String id, String videoCommitmentFile, String serviceAgreementFile);

    /** 企业管理员驳回旗下红娘资质申请 */
    void rejectStaff(String adminUserCode, String id, String rejectReason);

    /** 企业业绩汇总（旗下红娘账单汇总聚合） */
    DistEnterpriseBillStatsDTO getPerformanceStats(String adminUserCode, String distBizLineCode);

    /** 企业业绩明细分页（旗下红娘账单汇总） */
    IPage<DistUserBillSummaryDTO> pagePerformance(String adminUserCode, String distBizLineCode,
            Integer pageNo, Integer pageSize);

    /** 企业申请结算批次 */
    DistSettleBatchDTO applySettleBatch(String adminUserCode, String distBizLineCode);

    /** 企业结算批次分页 */
    IPage<DistSettleBatchDTO> pageSettleBatch(String adminUserCode, String distBizLineCode,
            Integer pageNo, Integer pageSize);

    /** 企业业绩-红娘消费结算明细分页 */
    IPage<DistUserBillSettleRecordDTO> pagePerformanceSettle(String adminUserCode, String distPayerUserCode,
            String distBizLineCode, Integer pageNo, Integer pageSize);

    /** 企业-红娘名下客户未结算佣金明细分页 */
    IPage<DistStaffSettleCustomerDTO> pageStaffSettleCustomers(String adminUserCode, String distMatchmakerUserCode,
            String distBizLineCode, Integer pageNo, Integer pageSize);

    /** 企业-红娘名下客户未结算佣金总额 */
    java.math.BigDecimal getStaffUnsettledCommissionTotal(String adminUserCode, String distMatchmakerUserCode,
            String distBizLineCode);

    /** 企业-按红娘申请结算 */
    DistSettleBatchDTO applyStaffSettleBatch(String adminUserCode, String distMatchmakerUserCode, String distBizLineCode);

    /** 企业-红娘结算批次分页 */
    IPage<DistSettleBatchDTO> pageStaffSettleBatch(String adminUserCode, String distMatchmakerUserCode,
            String distBizLineCode, Integer pageNo, Integer pageSize);
}
