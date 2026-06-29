package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.service.ApiDtMatchmakingCompanyService;
import pub.module.dating.api.service.dto.EnterpriseStaffAuditApproveVO;
import pub.module.dating.api.service.dto.EnterpriseStaffDTO;
import pub.module.dating.api.service.dto.MatchmakerAuditRejectVO;
import pub.module.dating.api.service.dto.MatchmakingCompanyApplyDTO;
import pub.module.dating.api.service.dto.MatchmakingCompanyApplySubmitVO;
import pub.module.distribution.api.service.dto.DistEnterpriseBillStatsDTO;
import pub.module.distribution.api.service.dto.DistSettleBatchDTO;
import pub.module.distribution.api.service.dto.DistStaffSettleCustomerDTO;
import pub.module.distribution.api.service.dto.DistUserBillSettleRecordDTO;
import pub.module.distribution.api.service.dto.DistUserBillSummaryDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

/**
 * 用户端-婚介公司入驻
 */
@Tag(name = "用户端-婚介公司入驻")
@RestController
@RequestMapping("/cus/dating/dtMatchmakingCompany")
@Slf4j
public class CusDtMatchmakingCompanyController {

    @Resource
    private ApiDtMatchmakingCompanyService apiDtMatchmakingCompanyService;

    @Operation(summary = "用户端-按当前管理员查询企业入驻信息")
    @GetMapping("/myAsAdmin")
    public Result<MatchmakingCompanyApplyDTO> myAsAdmin() {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.getMyCompanyAsAdmin(user.getUserCode()));
    }

    @Operation(summary = "用户端-提交企业入驻申请")
    @PostMapping("/submitApply")
    public Result<MatchmakingCompanyApplyDTO> submitApply(@RequestBody MatchmakingCompanyApplySubmitVO vo) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.submitApply(user.getUserCode(), vo));
    }

    @Operation(summary = "用户端-确认已完成对公认证转账")
    @PostMapping("/confirmTransfer")
    public Result<MatchmakingCompanyApplyDTO> confirmTransfer() {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.confirmTransfer(user.getUserCode()));
    }

    @Operation(summary = "用户端-企业管理员查询旗下红娘列表")
    @GetMapping("/listStaff")
    public Result<IPage<EnterpriseStaffDTO>> listStaff(
            @RequestParam(name = "auditTab", defaultValue = "pending") String auditTab,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.listStaff(user.getUserCode(), auditTab, pageNo, pageSize));
    }

    @Operation(summary = "用户端-企业管理员查询旗下红娘详情（审核用）")
    @GetMapping("/staff/queryById")
    public Result<EnterpriseStaffDTO> staffQueryById(@RequestParam("id") String id) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.getStaffDetail(user.getUserCode(), id));
    }

    @Operation(summary = "用户端-企业管理员审核通过旗下红娘（需上传附件，进入平台审核）")
    @PostMapping("/staff/approve")
    public Result<String> staffApprove(@RequestBody EnterpriseStaffAuditApproveVO vo) {
        UserDTO user = UserUtil.getCurrentSysUser();
        apiDtMatchmakingCompanyService.approveStaff(user.getUserCode(), vo.getId(),
                vo.getMkVideoCommitmentFile(), vo.getMkServiceAgreementFile());
        return Result.ok("审核通过，已提交平台审核");
    }

    @Operation(summary = "用户端-企业管理员驳回旗下红娘资质申请")
    @PostMapping("/staff/reject")
    public Result<String> staffReject(@RequestBody MatchmakerAuditRejectVO vo) {
        UserDTO user = UserUtil.getCurrentSysUser();
        apiDtMatchmakingCompanyService.rejectStaff(user.getUserCode(), vo.getId(), vo.getRejectReason());
        return Result.ok("已驳回");
    }

    @Operation(summary = "用户端-企业业绩汇总")
    @GetMapping("/performance/stats")
    public Result<DistEnterpriseBillStatsDTO> performanceStats(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.getPerformanceStats(user.getUserCode(), distBizLineCode));
    }

    @Operation(summary = "用户端-企业业绩明细分页")
    @GetMapping("/performance/page")
    public Result<IPage<DistUserBillSummaryDTO>> performancePage(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.pagePerformance(
                user.getUserCode(), distBizLineCode, pageNo, pageSize));
    }

    @Operation(summary = "用户端-企业业绩消费结算明细分页")
    @GetMapping("/performance/settle/page")
    public Result<IPage<DistUserBillSettleRecordDTO>> performanceSettlePage(
            @RequestParam("distPayerUserCode") String distPayerUserCode,
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.pagePerformanceSettle(
                user.getUserCode(), distPayerUserCode, distBizLineCode, pageNo, pageSize));
    }

    @Operation(summary = "用户端-企业申请结算")
    @PostMapping("/settleBatch/apply")
    public Result<DistSettleBatchDTO> applySettleBatch(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.applySettleBatch(user.getUserCode(), distBizLineCode));
    }

    @Operation(summary = "用户端-企业结算批次分页")
    @GetMapping("/settleBatch/page")
    public Result<IPage<DistSettleBatchDTO>> settleBatchPage(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.pageSettleBatch(
                user.getUserCode(), distBizLineCode, pageNo, pageSize));
    }

    @Operation(summary = "用户端-红娘名下客户未结算佣金总额")
    @GetMapping("/staff/settle/stats")
    public Result<java.math.BigDecimal> staffSettleStats(
            @RequestParam("distMatchmakerUserCode") String distMatchmakerUserCode,
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.getStaffUnsettledCommissionTotal(
                user.getUserCode(), distMatchmakerUserCode, distBizLineCode));
    }

    @Operation(summary = "用户端-红娘名下客户未结算佣金明细分页")
    @GetMapping("/staff/settle/customer/page")
    public Result<IPage<DistStaffSettleCustomerDTO>> staffSettleCustomerPage(
            @RequestParam("distMatchmakerUserCode") String distMatchmakerUserCode,
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.pageStaffSettleCustomers(
                user.getUserCode(), distMatchmakerUserCode, distBizLineCode, pageNo, pageSize));
    }

    @Operation(summary = "用户端-按红娘申请结算")
    @PostMapping("/staff/settleBatch/apply")
    public Result<DistSettleBatchDTO> applyStaffSettleBatch(
            @RequestParam("distMatchmakerUserCode") String distMatchmakerUserCode,
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.applyStaffSettleBatch(
                user.getUserCode(), distMatchmakerUserCode, distBizLineCode));
    }

    @Operation(summary = "用户端-红娘结算批次分页")
    @GetMapping("/staff/settleBatch/page")
    public Result<IPage<DistSettleBatchDTO>> staffSettleBatchPage(
            @RequestParam("distMatchmakerUserCode") String distMatchmakerUserCode,
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakingCompanyService.pageStaffSettleBatch(
                user.getUserCode(), distMatchmakerUserCode, distBizLineCode, pageNo, pageSize));
    }
}
