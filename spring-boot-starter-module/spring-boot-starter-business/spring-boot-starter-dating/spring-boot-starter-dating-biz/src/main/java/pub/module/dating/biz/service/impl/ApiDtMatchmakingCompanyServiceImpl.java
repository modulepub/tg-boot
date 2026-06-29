package pub.module.dating.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.IdentityApplyProcessCodeEnum;
import pub.module.dating.api.service.ApiDtMatchmakerMgtService;
import pub.module.dating.api.service.ApiDtMatchmakingCompanyService;
import pub.module.dating.api.service.dto.EnterpriseStaffDTO;
import pub.module.dating.api.service.dto.MatchmakingCompanyApplyDTO;
import pub.module.dating.api.service.dto.MatchmakingCompanyApplySubmitVO;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.entity.DtMatchmakingCompany;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.dating.crud.service.DtMatchmakingCompanyService;
import pub.module.distribution.api.service.ApiDistSettleBatchService;
import pub.module.distribution.api.service.ApiDistUserBillSummaryService;
import pub.module.distribution.api.service.dto.DistEnterpriseBillStatsDTO;
import pub.module.distribution.api.service.dto.DistSettleBatchDTO;
import pub.module.distribution.api.service.dto.DistStaffSettleCustomerDTO;
import pub.module.distribution.api.service.dto.DistUserBillSettleRecordDTO;
import pub.module.distribution.api.service.dto.DistUserBillSummaryDTO;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户端婚介公司入驻申请
 */
@Service
public class ApiDtMatchmakingCompanyServiceImpl implements ApiDtMatchmakingCompanyService {

    @Value("${dating.platform.company-name:山东省卿卿网络科技有限公司}")
    private String platformCompanyName;

    @Value("${dating.platform.public-account-no:}")
    private String platformPublicAccountNo;

    @Value("${dating.platform.bank-name:中国建设银行}")
    private String platformBankName;

    @Value("${dating.platform.bank-location:山东省日照市东港区}")
    private String platformBankLocation;

    @Resource
    private DtMatchmakingCompanyService dtMatchmakingCompanyService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private ApiDistUserBillSummaryService apiDistUserBillSummaryService;
    @Resource
    private ApiDistSettleBatchService apiDistSettleBatchService;
    @Resource
    private ApiDtMatchmakerMgtService apiDtMatchmakerMgtService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    public DistEnterpriseBillStatsDTO getPerformanceStats(String adminUserCode, String distBizLineCode) {
        List<String> userCodes = listCompanyMatchmakerUserCodes(requireCompany(adminUserCode));
        return apiDistUserBillSummaryService.getStatsByUserCodes(userCodes, distBizLineCode);
    }

    @Override
    public IPage<DistUserBillSummaryDTO> pagePerformance(String adminUserCode, String distBizLineCode,
            Integer pageNo, Integer pageSize) {
        List<String> userCodes = listCompanyMatchmakerUserCodes(requireCompany(adminUserCode));
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return apiDistUserBillSummaryService.pageByUserCodes(userCodes, distBizLineCode, safePageNo, safePageSize);
    }

    @Override
    public IPage<DistUserBillSettleRecordDTO> pagePerformanceSettle(String adminUserCode, String distPayerUserCode,
            String distBizLineCode, Integer pageNo, Integer pageSize) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        List<String> matchmakerCodes = listCompanyMatchmakerUserCodes(company);
        List<String> allowedPayerCodes = apiDistUserBillSummaryService.resolveSettlePayerUserCodes(
                matchmakerCodes, distBizLineCode);
        String payerCode = StrUtil.trim(distPayerUserCode);
        Assert.notBlank(payerCode, "用户编码不能为空");
        Assert.isTrue(allowedPayerCodes.contains(payerCode), "无权查看该用户消费明细");
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return apiDistUserBillSummaryService.pageSettleRecordsByPayer(payerCode, distBizLineCode, safePageNo, safePageSize);
    }

    @Override
    public DistSettleBatchDTO applySettleBatch(String adminUserCode, String distBizLineCode) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        List<String> matchmakerCodes = listCompanyMatchmakerUserCodes(company);
        List<String> payerUserCodes = apiDistUserBillSummaryService.resolveSettlePayerUserCodes(
                matchmakerCodes, distBizLineCode);
        DistEnterpriseBillStatsDTO stats = getPerformanceStats(adminUserCode, distBizLineCode);
        BigDecimal total = settlableAmount(stats.getDistPaidTotalAmount(), stats.getDistInServiceTotalAmount())
                .add(settlableAmount(stats.getDistSubPaidTotalAmount(), stats.getDistSubInServiceTotalAmount()));
        return apiDistSettleBatchService.apply(
                company.getMkCompanyCode(),
                company.getMkCompanyName(),
                adminUserCode,
                distBizLineCode,
                total,
                payerUserCodes);
    }

    @Override
    public IPage<DistSettleBatchDTO> pageSettleBatch(String adminUserCode, String distBizLineCode,
            Integer pageNo, Integer pageSize) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return apiDistSettleBatchService.pageByCompany(
                company.getMkCompanyCode(), distBizLineCode, safePageNo, safePageSize);
    }

    @Override
    public BigDecimal getStaffUnsettledCommissionTotal(String adminUserCode, String distMatchmakerUserCode,
            String distBizLineCode) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        String matchmakerCode = requireCompanyMatchmakerUserCode(company, distMatchmakerUserCode);
        return apiDistUserBillSummaryService.sumStaffUnsettledCommission(matchmakerCode, distBizLineCode);
    }

    @Override
    public IPage<DistStaffSettleCustomerDTO> pageStaffSettleCustomers(String adminUserCode,
            String distMatchmakerUserCode, String distBizLineCode, Integer pageNo, Integer pageSize) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        String matchmakerCode = requireCompanyMatchmakerUserCode(company, distMatchmakerUserCode);
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return apiDistUserBillSummaryService.pageStaffSettleCustomers(
                matchmakerCode, distBizLineCode, safePageNo, safePageSize);
    }

    @Override
    public DistSettleBatchDTO applyStaffSettleBatch(String adminUserCode, String distMatchmakerUserCode,
            String distBizLineCode) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        String matchmakerCode = requireCompanyMatchmakerUserCode(company, distMatchmakerUserCode);
        List<String> payerUserCodes = apiDistUserBillSummaryService.resolveStaffSettlePayerUserCodes(
                matchmakerCode, distBizLineCode);
        BigDecimal total = apiDistUserBillSummaryService.sumStaffUnsettledCommission(matchmakerCode, distBizLineCode);
        return apiDistSettleBatchService.applyForStaff(
                company.getMkCompanyCode(),
                company.getMkCompanyName(),
                adminUserCode,
                matchmakerCode,
                distBizLineCode,
                total,
                payerUserCodes);
    }

    @Override
    public IPage<DistSettleBatchDTO> pageStaffSettleBatch(String adminUserCode, String distMatchmakerUserCode,
            String distBizLineCode, Integer pageNo, Integer pageSize) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        String matchmakerCode = requireCompanyMatchmakerUserCode(company, distMatchmakerUserCode);
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return apiDistSettleBatchService.pageByCompanyAndStaff(
                company.getMkCompanyCode(), matchmakerCode, distBizLineCode, safePageNo, safePageSize);
    }

    private String requireCompanyMatchmakerUserCode(DtMatchmakingCompany company, String distMatchmakerUserCode) {
        String matchmakerCode = StrUtil.trim(distMatchmakerUserCode);
        Assert.notBlank(matchmakerCode, "红娘用户编码不能为空");
        List<String> matchmakerCodes = listCompanyMatchmakerUserCodes(company);
        Assert.isTrue(matchmakerCodes.contains(matchmakerCode), "无权操作该红娘");
        return matchmakerCode;
    }

    private static BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    /** 付费总额减去仍处服务期内的金额，作为可申请结算的基数。 */
    private static BigDecimal settlableAmount(BigDecimal paid, BigDecimal inService) {
        BigDecimal result = defaultAmount(paid).subtract(defaultAmount(inService));
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }

    private DtMatchmakingCompany requireCompany(String adminUserCode) {
        Assert.notBlank(adminUserCode, "请先登录");
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getByAdminUserCode(adminUserCode);
        Assert.notNull(company, "未找到企业信息，请先完成企业入驻");
        return company;
    }

    private List<String> listCompanyMatchmakerUserCodes(DtMatchmakingCompany company) {
        QueryWrapper<DtMatchmaker> queryWrapper = new QueryWrapper<>();
        applyCompanyScope(queryWrapper, company);
        queryWrapper.select("mk_user_code");
        return dtMatchmakerService.list(queryWrapper).stream()
                .map(DtMatchmaker::getMkUserCode)
                .filter(StrUtil::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
    }

    @Override
    public IPage<EnterpriseStaffDTO> listStaff(String adminUserCode, String auditTab, Integer pageNo, Integer pageSize) {
        Assert.notBlank(adminUserCode, "请先登录");
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getByAdminUserCode(adminUserCode);
        Assert.notNull(company, "未找到企业信息，请先完成企业入驻");

        QueryWrapper<DtMatchmaker> queryWrapper = new QueryWrapper<>();
        applyCompanyScope(queryWrapper, company);
        applyStaffAuditTab(queryWrapper, auditTab);
        queryWrapper.orderByDesc("create_time");

        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        return dtMatchmakerService.page(new Page<>(safePageNo, safePageSize), queryWrapper)
                .convert(this::toStaffDto);
    }

    @Override
    public EnterpriseStaffDTO getStaffDetail(String adminUserCode, String id) {
        DtMatchmaker matchmaker = requireStaffInCompanyScope(adminUserCode, id);
        return toStaffDetailDto(matchmaker);
    }

    @Override
    public void approveStaff(String adminUserCode, String id, String videoCommitmentFile, String serviceAgreementFile) {
        requireStaffInCompanyScope(adminUserCode, id);
        apiDtMatchmakerMgtService.approveByEnterprise(id, adminUserCode, videoCommitmentFile, serviceAgreementFile);
    }

    @Override
    public void rejectStaff(String adminUserCode, String id, String rejectReason) {
        requireStaffInCompanyScope(adminUserCode, id);
        apiDtMatchmakerMgtService.rejectByEnterprise(id, rejectReason, adminUserCode);
    }

    private DtMatchmaker requireStaffInCompanyScope(String adminUserCode, String staffId) {
        DtMatchmakingCompany company = requireCompany(adminUserCode);
        Assert.notBlank(staffId, "红娘 id 不能为空");
        DtMatchmaker matchmaker = dtMatchmakerService.getById(staffId);
        Assert.notNull(matchmaker, "红娘不存在");
        QueryWrapper<DtMatchmaker> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", staffId);
        applyCompanyScope(queryWrapper, company);
        Assert.isTrue(dtMatchmakerService.count(queryWrapper) > 0, "无权操作该红娘");
        return matchmaker;
    }

    private EnterpriseStaffDTO toStaffDto(DtMatchmaker matchmaker) {
        EnterpriseStaffDTO dto = new EnterpriseStaffDTO();
        if (matchmaker == null) {
            return dto;
        }
        dto.setId(matchmaker.getId());
        dto.setMkCode(matchmaker.getMkCode());
        dto.setMkWorkPhoto(matchmaker.getMkWorkPhoto());
        dto.setMkName(matchmaker.getMkName());
        dto.setMkAge(matchmaker.getMkAge());
        dto.setMkPhone(matchmaker.getMkPhone());
        dto.setMkCityName(matchmaker.getMkCityName());
        dto.setMkTags(matchmaker.getMkTags());
        dto.setMkMoment(matchmaker.getMkMoment());
        dto.setMkIdentityStatusCode(statusCodeValue(matchmaker.getMkIdentityStatusCode()));
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(matchmaker.getMkIdentityProcessCode());
        dto.setMkIdentityProcessCode(process.getCode());
        dto.setCreateTime(matchmaker.getCreateTime());
        return dto;
    }

    private EnterpriseStaffDTO toStaffDetailDto(DtMatchmaker matchmaker) {
        EnterpriseStaffDTO dto = toStaffDto(matchmaker);
        dto.setMkUserCode(matchmaker.getMkUserCode());
        dto.setMkIdNo(matchmaker.getMkIdNo());
        dto.setMkCompanyName(matchmaker.getMkCompanyName());
        dto.setMkChannelsFinderUserName(matchmaker.getMkChannelsFinderUserName());
        dto.setMkIdentityRejectReason(matchmaker.getMkIdentityRejectReason());
        dto.setMkIdentityAuditBy(matchmaker.getMkIdentityAuditBy());
        dto.setMkIdentityAuditAt(matchmaker.getMkIdentityAuditAt());
        dto.setMkVideoCommitmentFile(matchmaker.getMkVideoCommitmentFile());
        dto.setMkServiceAgreementFile(matchmaker.getMkServiceAgreementFile());
        dto.setMkEnterpriseAuditBy(matchmaker.getMkEnterpriseAuditBy());
        dto.setMkEnterpriseAuditAt(matchmaker.getMkEnterpriseAuditAt());
        dto.setMkPlatformRejectReason(matchmaker.getMkPlatformRejectReason());
        dto.setMkPlatformAuditBy(matchmaker.getMkPlatformAuditBy());
        dto.setMkPlatformAuditAt(matchmaker.getMkPlatformAuditAt());
        return dto;
    }

    private static void applyCompanyScope(QueryWrapper<DtMatchmaker> queryWrapper, DtMatchmakingCompany company) {
        String companyCode = StrUtil.trim(company.getMkCompanyCode());
        String companyName = StrUtil.trim(company.getMkCompanyName());
        if (StrUtil.isNotBlank(companyCode) && StrUtil.isNotBlank(companyName)) {
            queryWrapper.and(w -> w.eq("mk_company_code", companyCode)
                    .or()
                    .eq("mk_company_name", companyName));
            return;
        }
        if (StrUtil.isNotBlank(companyCode)) {
            queryWrapper.eq("mk_company_code", companyCode);
            return;
        }
        if (StrUtil.isNotBlank(companyName)) {
            queryWrapper.eq("mk_company_name", companyName);
            return;
        }
        queryWrapper.apply("1 = 0");
    }

    private static void applyStaffAuditTab(QueryWrapper<DtMatchmaker> queryWrapper, String auditTab) {
        if ("approved".equalsIgnoreCase(StrUtil.trim(auditTab))) {
            queryWrapper.in("mk_identity_process_code",
                    IdentityApplyProcessCodeEnum.PLATFORM_REVIEWING.getCode(),
                    IdentityApplyProcessCodeEnum.APPROVED.getCode());
            return;
        }
        queryWrapper.in("mk_identity_process_code",
                IdentityApplyProcessCodeEnum.REVIEWING.getCode(),
                IdentityApplyProcessCodeEnum.REJECTED.getCode());
    }

    @Override
    public MatchmakingCompanyApplyDTO getMyCompanyAsAdmin(String adminUserCode) {
        Assert.notBlank(adminUserCode, "请先登录");
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getByAdminUserCode(adminUserCode);
        return toApplyDto(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchmakingCompanyApplyDTO submitApply(String adminUserCode, MatchmakingCompanyApplySubmitVO vo) {
        Assert.notBlank(adminUserCode, "请先登录");
        validateSubmit(vo);

        DtMatchmakingCompany existing = dtMatchmakingCompanyService.getByAdminUserCode(adminUserCode);
        if (existing != null && StatusCodeEnum.isYesValue(existing.getMkCompanyIdentityStatusCode())) {
            throw new IllegalArgumentException("企业已通过认证，无需重复提交");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(existing != null
                ? existing.getMkCompanyIdentityProcessCode() : null);
        if (existing != null && process == IdentityApplyProcessCodeEnum.REVIEWING) {
            throw new IllegalArgumentException("申请审核中，请耐心等待");
        }

        boolean resubmitAfterReject = existing != null && process == IdentityApplyProcessCodeEnum.REJECTED;
        BigDecimal verifyAmount = existing != null ? existing.getMkCompanyVerifyAmount() : null;
        boolean needTransfer = !resubmitAfterReject || verifyAmount == null;
        if (needTransfer) {
            verifyAmount = randomVerifyAmount();
        }

        DtMatchmakingCompany entity = existing != null ? existing : new DtMatchmakingCompany();
        if (existing == null) {
            String userCode = adminUserCode.trim();
            entity.setMkCompanyAdminUserCode(userCode);
            entity.setMkCompanyAdminUserRealName(resolveAdminUserRealName(apiSysUserService.getUserByUserCode(userCode)));
            entity.setMkCompanyIdentityStatusCode(StatusCodeEnum.NO);
            entity.setMkCompanyIdentityProcessCode(IdentityApplyProcessCodeEnum.DRAFT);
        }
        copySubmitFields(entity, vo);
        entity.setMkCompanyVerifyAmount(verifyAmount);
        entity.setMkCompanyVerifySkipCode(needTransfer ? null : "1");
        entity.setMkCompanyTransferStatusCode(needTransfer ? StatusCodeEnum.NO : StatusCodeEnum.YES);
        entity.setMkCompanyIdentityStatusCode(StatusCodeEnum.NO);
        entity.setMkCompanyIdentityProcessCode(IdentityApplyProcessCodeEnum.REVIEWING);
        entity.setMkCompanyRejectReason(null);

        if (existing == null) {
            dtMatchmakingCompanyService.save(entity);
        }
        else {
            dtMatchmakingCompanyService.updateById(entity);
        }

        MatchmakingCompanyApplyDTO dto = toApplyDto(entity);
        dto.setNeedTransfer(needTransfer);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchmakingCompanyApplyDTO confirmTransfer(String adminUserCode) {
        Assert.notBlank(adminUserCode, "请先登录");
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getByAdminUserCode(adminUserCode);
        Assert.notNull(company, "未找到企业入驻申请");
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(company.getMkCompanyIdentityProcessCode());
        if (process != IdentityApplyProcessCodeEnum.REVIEWING) {
            throw new IllegalArgumentException("仅审核中的申请可确认转账");
        }
        if (!shouldNeedTransfer(company, process)) {
            throw new IllegalArgumentException("当前申请无需转账确认");
        }
        if (StatusCodeEnum.isYesValue(company.getMkCompanyTransferStatusCode())) {
            throw new IllegalArgumentException("您已确认转账，请勿重复操作");
        }
        company.setMkCompanyTransferStatusCode(StatusCodeEnum.YES);
        dtMatchmakingCompanyService.updateById(company);
        return toApplyDto(company);
    }

    private void validateSubmit(MatchmakingCompanyApplySubmitVO vo) {
        Assert.notNull(vo, "请填写申请信息");
        Assert.notBlank(vo.getMkCompanyName(), "请填写企业名称");
        Assert.notBlank(vo.getMkCompanyUsciCode(), "请填写统一社会信用代码");
        Assert.notBlank(vo.getMkCompanyTel(), "请填写公司电话");
        Assert.notBlank(vo.getMkCompanyLegalName(), "请填写法人姓名");
        Assert.notBlank(vo.getMkCompanyLegalIdNo(), "请填写法人证件号");
        Assert.notBlank(vo.getMkCompanyCityCode(), "请选择所在城市");
        Assert.notBlank(vo.getMkCompanyCityName(), "请选择所在城市");
        Assert.notBlank(vo.getMkCompanyAddressDetail(), "请填写公司地址");
        Assert.notBlank(vo.getMkCompanyPhotos(), "请上传办公/门头照片");
        Assert.notBlank(vo.getMkCompanyPublicAccountNo(), "请填写对公银行账号");
        Assert.notBlank(vo.getMkCompanyBankName(), "请填写开户行");
        Assert.notBlank(vo.getMkCompanyBankLocation(), "请填写开户地");
    }

    private void copySubmitFields(DtMatchmakingCompany entity, MatchmakingCompanyApplySubmitVO vo) {
        entity.setMkCompanyName(trim(vo.getMkCompanyName()));
        entity.setMkCompanyUsciCode(trim(vo.getMkCompanyUsciCode()));
        entity.setMkCompanyTel(trim(vo.getMkCompanyTel()));
        entity.setMkCompanyLegalName(trim(vo.getMkCompanyLegalName()));
        entity.setMkCompanyLegalIdNo(trim(vo.getMkCompanyLegalIdNo()));
        entity.setMkCompanyAddressDetail(trim(vo.getMkCompanyAddressDetail()));
        entity.setMkCompanyCityCode(trim(vo.getMkCompanyCityCode()));
        entity.setMkCompanyCityName(trim(vo.getMkCompanyCityName()));
        entity.setMkCompanyPhotos(trim(vo.getMkCompanyPhotos()));
        entity.setMkCompanyPublicAccountNo(trim(vo.getMkCompanyPublicAccountNo()));
        entity.setMkCompanyBankName(trim(vo.getMkCompanyBankName()));
        entity.setMkCompanyBankLocation(trim(vo.getMkCompanyBankLocation()));
    }

    private MatchmakingCompanyApplyDTO toApplyDto(DtMatchmakingCompany company) {
        MatchmakingCompanyApplyDTO dto = new MatchmakingCompanyApplyDTO();
        fillPlatformInfo(dto);
        if (company == null) {
            dto.setHasRecord(false);
            dto.setNeedTransfer(true);
            dto.setMkCompanyIdentityStatusCode(StatusCodeEnum.NO.getCode());
            dto.setMkCompanyIdentityProcessCode(IdentityApplyProcessCodeEnum.DRAFT.getCode());
            fillProcessFlags(dto, null, StatusCodeEnum.NO);
            return dto;
        }

        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(company.getMkCompanyIdentityProcessCode());
        StatusCodeEnum status = company.getMkCompanyIdentityStatusCode();

        dto.setHasRecord(true);
        dto.setMkCompanyCode(company.getMkCompanyCode());
        dto.setMkCompanyName(company.getMkCompanyName());
        dto.setMkCompanyTel(company.getMkCompanyTel());
        dto.setMkCompanyUsciCode(company.getMkCompanyUsciCode());
        dto.setMkCompanyLegalName(company.getMkCompanyLegalName());
        dto.setMkCompanyLegalIdNo(company.getMkCompanyLegalIdNo());
        dto.setMkCompanyAddressDetail(company.getMkCompanyAddressDetail());
        dto.setMkCompanyCityCode(company.getMkCompanyCityCode());
        dto.setMkCompanyCityName(company.getMkCompanyCityName());
        dto.setMkCompanyPhotos(company.getMkCompanyPhotos());
        dto.setMkCompanyPublicAccountNo(company.getMkCompanyPublicAccountNo());
        dto.setMkCompanyBankName(company.getMkCompanyBankName());
        dto.setMkCompanyBankLocation(company.getMkCompanyBankLocation());
        dto.setMkCompanyVerifyAmount(company.getMkCompanyVerifyAmount());
        dto.setMkCompanyRejectReason(company.getMkCompanyRejectReason());
        dto.setMkCompanyIdentityStatusCode(statusCodeValue(status));
        dto.setMkCompanyIdentityProcessCode(process.getCode());
        fillProcessFlags(dto, process, status);
        dto.setNeedTransfer(shouldNeedTransfer(company, process));
        fillTransferFlags(dto, company, process);
        return dto;
    }

    private static void fillTransferFlags(MatchmakingCompanyApplyDTO dto, DtMatchmakingCompany company,
                                          IdentityApplyProcessCodeEnum process) {
        boolean transferred = isTransferred(company);
        dto.setMkCompanyTransferStatusCode(transferred ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode());
        dto.setTransferred(transferred);
        dto.setTransferStatusLabel(transferStatusLabel(company, process, transferred));
    }

    private static boolean isTransferred(DtMatchmakingCompany company) {
        if (company == null) {
            return false;
        }
        if ("1".equals(String.valueOf(company.getMkCompanyVerifySkipCode()).trim())) {
            return true;
        }
        return StatusCodeEnum.isYesValue(company.getMkCompanyTransferStatusCode());
    }

    private static String transferStatusLabel(DtMatchmakingCompany company,
                                              IdentityApplyProcessCodeEnum process,
                                              boolean transferred) {
        if ("1".equals(String.valueOf(company != null ? company.getMkCompanyVerifySkipCode() : null).trim())) {
            return "无需再次转账";
        }
        IdentityApplyProcessCodeEnum p = IdentityApplyProcessCodeEnum.effective(process);
        if (p != IdentityApplyProcessCodeEnum.REVIEWING) {
            return transferred ? "已转账" : "未转账";
        }
        return transferred ? "已转账" : "未转账";
    }

    private static void fillProcessFlags(MatchmakingCompanyApplyDTO dto,
                                         IdentityApplyProcessCodeEnum process,
                                         StatusCodeEnum status) {
        IdentityApplyProcessCodeEnum p = IdentityApplyProcessCodeEnum.effective(process);
        dto.setCertified(StatusCodeEnum.isYesValue(status));
        dto.setPending(p == IdentityApplyProcessCodeEnum.REVIEWING);
        dto.setRejected(p == IdentityApplyProcessCodeEnum.REJECTED);
        dto.setDraft(p == IdentityApplyProcessCodeEnum.DRAFT);
        dto.setAuditStatusLabel(IdentityApplyProcessCodeEnum.label(p));
    }

    private boolean shouldNeedTransfer(DtMatchmakingCompany company, IdentityApplyProcessCodeEnum process) {
        if (company == null || company.getMkCompanyVerifyAmount() == null) {
            return false;
        }
        if ("1".equals(String.valueOf(company.getMkCompanyVerifySkipCode()).trim())) {
            return false;
        }
        return process == IdentityApplyProcessCodeEnum.REVIEWING;
    }

    private void fillPlatformInfo(MatchmakingCompanyApplyDTO dto) {
        dto.setPlatformCompanyName(platformCompanyName);
        DtMatchmakingCompany platform = findPlatformCompany();
        dto.setPlatformPublicAccountNo(firstNonBlank(
                validPlatformAccount(platform),
                trim(platformPublicAccountNo)));
        dto.setPlatformBankName(firstNonBlank(
                platform != null ? trim(platform.getMkCompanyBankName()) : null,
                trim(platformBankName)));
        dto.setPlatformBankLocation(firstNonBlank(
                platform != null ? trim(platform.getMkCompanyBankLocation()) : null,
                trim(platformBankLocation)));
    }

    private DtMatchmakingCompany findPlatformCompany() {
        return dtMatchmakingCompanyService.getOne(new QueryWrapper<DtMatchmakingCompany>().lambda()
                .eq(DtMatchmakingCompany::getMkCompanyName, platformCompanyName)
                .last("LIMIT 1"), false);
    }

    private static String validPlatformAccount(DtMatchmakingCompany platform) {
        if (platform == null) {
            return null;
        }
        String account = trim(platform.getMkCompanyPublicAccountNo());
        if (StrUtil.isBlank(account)) {
            return null;
        }
        String usci = trim(platform.getMkCompanyUsciCode());
        if (StrUtil.isNotBlank(usci) && account.equalsIgnoreCase(usci)) {
            return null;
        }
        if (account.startsWith("6228480000000000")) {
            return null;
        }
        return account;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private static String statusCodeValue(StatusCodeEnum status) {
        return StatusCodeEnum.isYesValue(status) ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode();
    }

    private static BigDecimal randomVerifyAmount() {
        int cents = ThreadLocalRandom.current().nextInt(1, 101);
        BigDecimal result = BigDecimal.valueOf(cents).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if(cents>1){
            result = new BigDecimal(300);
        }
        return result;
    }



    private static String trim(String value) {
        return StrUtil.trim(value);
    }

    private static String resolveAdminUserRealName(UserDTO user) {
        if (user == null) {
            return null;
        }
        if (StrUtil.isNotBlank(user.getUserRealName())) {
            return user.getUserRealName().trim();
        }
        if (StrUtil.isNotBlank(user.getUserName())) {
            return user.getUserName().trim();
        }
        return null;
    }
}
