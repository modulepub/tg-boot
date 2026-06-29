package pub.module.dating.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.constants.IdentityApplyProcessCodeEnum;
import pub.module.dating.api.messaging.DtMatchmakingCompanyUpdatedMessage;
import pub.module.dating.api.service.ApiDtMatchmakingCompanyMgtService;
import pub.module.dating.api.service.dto.MatchmakingCompanyMgtEditVO;
import pub.module.dating.api.service.dto.MatchmakingCompanyRedundantDTO;
import pub.module.dating.biz.messaging.DtMatchmakingCompanyUpdatedPublisher;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.entity.DtMatchmakingCompany;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.dating.crud.service.DtMatchmakingCompanyService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 管理端婚介公司入驻审核。
 */
@Service
public class ApiDtMatchmakingCompanyMgtServiceImpl implements ApiDtMatchmakingCompanyMgtService {

    @Resource
    private DtMatchmakingCompanyService dtMatchmakingCompanyService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private ApiSysUserService apiSysUserService;
    @Resource
    private DtMatchmakingCompanyUpdatedPublisher dtMatchmakingCompanyUpdatedPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String id, String auditBy) {
        DtMatchmakingCompany company = requireReviewing(id);
        LocalDateTime now = LocalDateTime.now();
        company.setMkCompanyIdentityStatusCode(StatusCodeEnum.YES);
        company.setMkCompanyIdentityProcessCode(IdentityApplyProcessCodeEnum.APPROVED);
        company.setMkCompanyRejectReason(null);
        company.setMkCompanyAuditBy(StrUtil.trim(auditBy));
        company.setMkCompanyAuditAt(now);
        dtMatchmakingCompanyService.updateById(company);
        syncMatchmakerCompanyCode(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApplyInfo(MatchmakingCompanyMgtEditVO vo) {
        DtMatchmakingCompany company = requireCompanyExists(vo != null ? vo.getId() : null);
        copyEditFields(company, vo);
        dtMatchmakingCompanyService.updateById(company);
        publishCompanyUpdated(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitForReview(String id) {
        DtMatchmakingCompany company = requireSubmittable(id);
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(
                company.getMkCompanyIdentityProcessCode());
        boolean resubmitAfterReject = process == IdentityApplyProcessCodeEnum.REJECTED;
        BigDecimal verifyAmount = company.getMkCompanyVerifyAmount();
        boolean needTransfer = !resubmitAfterReject || verifyAmount == null;
        if (needTransfer) {
            verifyAmount = randomVerifyAmount();
        }
        company.setMkCompanyVerifyAmount(verifyAmount);
        company.setMkCompanyVerifySkipCode(needTransfer ? null : "1");
        company.setMkCompanyTransferStatusCode(needTransfer ? StatusCodeEnum.NO : StatusCodeEnum.YES);
        company.setMkCompanyIdentityStatusCode(StatusCodeEnum.NO);
        company.setMkCompanyIdentityProcessCode(IdentityApplyProcessCodeEnum.REVIEWING);
        dtMatchmakingCompanyService.updateById(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAdmin(String id, String adminUserCode) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("企业 id 不能为空");
        }
        if (StrUtil.isBlank(adminUserCode)) {
            throw new IllegalArgumentException("请选择管理员用户");
        }
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getById(id);
        if (company == null) {
            throw new IllegalArgumentException("企业不存在");
        }
        String userCode = adminUserCode.trim();
        UserDTO user = apiSysUserService.getUserByUserCode(userCode);
        if (user == null) {
            throw new IllegalArgumentException("所选用户不存在");
        }
        DtMatchmakingCompany occupied = dtMatchmakingCompanyService.getByAdminUserCode(userCode);
        if (occupied != null && !StrUtil.equals(occupied.getId(), id)) {
            throw new IllegalArgumentException("该用户已是其他企业的管理员");
        }
        company.setMkCompanyAdminUserCode(userCode);
        company.setMkCompanyAdminUserRealName(resolveAdminUserRealName(user));
        dtMatchmakingCompanyService.updateById(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String id, String rejectReason, String auditBy) {
        DtMatchmakingCompany company = requireReviewing(id);
        if (StrUtil.isBlank(rejectReason)) {
            throw new IllegalArgumentException("请填写驳回原因");
        }
        LocalDateTime now = LocalDateTime.now();
        company.setMkCompanyIdentityStatusCode(StatusCodeEnum.NO);
        company.setMkCompanyIdentityProcessCode(IdentityApplyProcessCodeEnum.REJECTED);
        company.setMkCompanyRejectReason(StrUtil.trim(rejectReason));
        company.setMkCompanyAuditBy(StrUtil.trim(auditBy));
        company.setMkCompanyAuditAt(now);
        dtMatchmakingCompanyService.updateById(company);
    }

    private DtMatchmakingCompany requireCompanyExists(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("企业 id 不能为空");
        }
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getById(id);
        if (company == null) {
            throw new IllegalArgumentException("企业不存在");
        }
        return company;
    }

    private void copyEditFields(DtMatchmakingCompany entity, MatchmakingCompanyMgtEditVO vo) {
        if (vo == null) {
            return;
        }
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

    private DtMatchmakingCompany requireSubmittable(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("企业 id 不能为空");
        }
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getById(id);
        if (company == null) {
            throw new IllegalArgumentException("企业不存在");
        }
        if (StatusCodeEnum.isYesValue(company.getMkCompanyIdentityStatusCode())) {
            throw new IllegalArgumentException("企业已通过认证，无需重复提交");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(
                company.getMkCompanyIdentityProcessCode());
        if (!IdentityApplyProcessCodeEnum.canSubmit(process)) {
            throw new IllegalArgumentException("仅待提交或已驳回的申请可代提交");
        }
        validateCompanyFields(company);
        return company;
    }

    private void validateCompanyFields(DtMatchmakingCompany company) {
        if (StrUtil.isBlank(company.getMkCompanyName())) {
            throw new IllegalArgumentException("请填写企业名称");
        }
        if (StrUtil.isBlank(company.getMkCompanyUsciCode())) {
            throw new IllegalArgumentException("请填写统一社会信用代码");
        }
        if (StrUtil.isBlank(company.getMkCompanyTel())) {
            throw new IllegalArgumentException("请填写公司电话");
        }
        if (StrUtil.isBlank(company.getMkCompanyLegalName())) {
            throw new IllegalArgumentException("请填写法人姓名");
        }
        if (StrUtil.isBlank(company.getMkCompanyLegalIdNo())) {
            throw new IllegalArgumentException("请填写法人证件号");
        }
        if (StrUtil.isBlank(company.getMkCompanyCityCode())) {
            throw new IllegalArgumentException("请选择所在城市");
        }
        if (StrUtil.isBlank(company.getMkCompanyCityName())) {
            throw new IllegalArgumentException("请选择所在城市");
        }
        if (StrUtil.isBlank(company.getMkCompanyAddressDetail())) {
            throw new IllegalArgumentException("请填写公司地址");
        }
        if (StrUtil.isBlank(company.getMkCompanyPhotos())) {
            throw new IllegalArgumentException("请上传办公/门头照片");
        }
        if (StrUtil.isBlank(company.getMkCompanyPublicAccountNo())) {
            throw new IllegalArgumentException("请填写对公银行账号");
        }
        if (StrUtil.isBlank(company.getMkCompanyBankName())) {
            throw new IllegalArgumentException("请填写开户行");
        }
        if (StrUtil.isBlank(company.getMkCompanyBankLocation())) {
            throw new IllegalArgumentException("请填写开户地");
        }
    }

    private static BigDecimal randomVerifyAmount() {
        int cents = ThreadLocalRandom.current().nextInt(1, 101);
        return BigDecimal.valueOf(cents).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private DtMatchmakingCompany requireReviewing(String id) {
        if (StrUtil.isBlank(id)) {
            throw new IllegalArgumentException("企业 id 不能为空");
        }
        DtMatchmakingCompany company = dtMatchmakingCompanyService.getById(id);
        if (company == null) {
            throw new IllegalArgumentException("企业不存在");
        }
        IdentityApplyProcessCodeEnum process = IdentityApplyProcessCodeEnum.effective(
                company.getMkCompanyIdentityProcessCode());
        if (process != IdentityApplyProcessCodeEnum.REVIEWING) {
            throw new IllegalArgumentException("仅审核中的申请可操作");
        }
        return company;
    }

    private void publishCompanyUpdated(DtMatchmakingCompany company) {
        MatchmakingCompanyRedundantDTO dto = toRedundantDto(company);
        if (dto == null || StrUtil.isBlank(dto.getMkCompanyCode())) {
            return;
        }
        dtMatchmakingCompanyUpdatedPublisher.publishAfterCommit(
                new DtMatchmakingCompanyUpdatedMessage(dto));
    }

    private static MatchmakingCompanyRedundantDTO toRedundantDto(DtMatchmakingCompany company) {
        if (company == null) {
            return null;
        }
        MatchmakingCompanyRedundantDTO dto = new MatchmakingCompanyRedundantDTO();
        dto.setMkCompanyCode(trim(company.getMkCompanyCode()));
        dto.setMkCompanyName(trim(company.getMkCompanyName()));
        return dto;
    }

    private void syncMatchmakerCompanyCode(DtMatchmakingCompany company) {
        String companyCode = StrUtil.trim(company.getMkCompanyCode());
        String companyName = StrUtil.trim(company.getMkCompanyName());
        if (StrUtil.isBlank(companyCode) || StrUtil.isBlank(companyName)) {
            return;
        }
        UpdateWrapper<DtMatchmaker> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("mk_company_code", companyCode);
        updateWrapper.eq("mk_company_name", companyName);
        dtMatchmakerService.update(updateWrapper);
    }
}
