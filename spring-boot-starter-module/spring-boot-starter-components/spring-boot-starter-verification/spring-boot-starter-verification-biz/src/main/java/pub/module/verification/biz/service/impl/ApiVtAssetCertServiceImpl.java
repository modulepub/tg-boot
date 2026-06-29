package pub.module.verification.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.dating.api.service.dto.MatchmakerBriefDTO;
import pub.module.verification.api.constants.VtAssetCertProcessCodeEnum;
import pub.module.verification.api.dto.VtAssetCertRecordDTO;
import pub.module.verification.api.dto.VtAssetCertSubmitVO;
import pub.module.verification.api.service.ApiVtAssetCertService;
import pub.module.verification.crud.entity.VtAssetCertRecord;
import pub.module.verification.crud.service.VtAssetCertRecordService;

/**
 * 用户端-资产认证（爱与诚辅助认证）
 */
@Service
public class ApiVtAssetCertServiceImpl implements ApiVtAssetCertService {

    @Resource
    private VtAssetCertRecordService vtAssetCertRecordService;
    @Resource
    private ApiDtMatchmakerService apiDtMatchmakerService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VtAssetCertRecordDTO submitApply(String mkUserCode, VtAssetCertSubmitVO vo) {
        if (vo == null) {
            throw new IllegalArgumentException("提交内容不能为空");
        }
        String cusCode = StrUtil.trim(vo.getCusCode());
        String vehiclePhoto = StrUtil.trim(vo.getVehicleLicensePhoto());
        String housePhoto = StrUtil.trim(vo.getRealEstateCertificatePhoto());
        String maritalPhoto = StrUtil.trim(vo.getMaritalStatusProofPhoto());
        String honestyVideo = StrUtil.trim(vo.getHonestyVideoFile());
        if (StrUtil.isBlank(cusCode)) {
            throw new IllegalArgumentException("客户编码不能为空");
        }
        if (StrUtil.isBlank(vehiclePhoto)) {
            throw new IllegalArgumentException("请上传行驶证照片");
        }
        if (StrUtil.isBlank(housePhoto)) {
            throw new IllegalArgumentException("请上传房产证照片");
        }
        if (StrUtil.isBlank(maritalPhoto)) {
            throw new IllegalArgumentException("请上传婚姻状态证明照片");
        }
        if (StrUtil.isBlank(honestyVideo)) {
            throw new IllegalArgumentException("请上传诚实守信录制视频");
        }
        apiDtMatchmakerService.assertMkUserServesCustomer(mkUserCode, cusCode);

        VtAssetCertRecord reviewing = vtAssetCertRecordService.getOne(
                new QueryWrapper<VtAssetCertRecord>().lambda()
                        .eq(VtAssetCertRecord::getCusCode, cusCode)
                        .eq(VtAssetCertRecord::getAssetCertProcessCode, VtAssetCertProcessCodeEnum.REVIEWING)
                        .orderByDesc(VtAssetCertRecord::getCreateTime)
                        .last("LIMIT 1"),
                false);
        if (reviewing != null) {
            throw new IllegalArgumentException("该客户已有审核中的申请，请耐心等待");
        }

        DtCustomerDTO customer = apiDtCustomerService.getCusByCusCode(cusCode);
        if (customer == null) {
            throw new IllegalArgumentException("客户不存在");
        }
        MatchmakerBriefDTO matchmaker = apiDtMatchmakerService.getMatchmakerBriefByUserCode(mkUserCode);
        if (matchmaker == null) {
            throw new IllegalArgumentException("红娘信息不存在");
        }

        VtAssetCertRecord latest = getLatestRecord(cusCode);
        VtAssetCertRecord record;
        if (latest != null && VtAssetCertProcessCodeEnum.canSubmit(latest.getAssetCertProcessCode())) {
            record = latest;
        }
        else {
            record = new VtAssetCertRecord();
            record.setCusCode(cusCode);
        }
        record.setCusNickName(StrUtil.firstNonBlank(customer.getCusNickName(), customer.getCusName()));
        record.setSubmitMkCode(matchmaker.getMkCode());
        record.setSubmitMkName(matchmaker.getMkName());
        record.setVehicleLicensePhoto(vehiclePhoto);
        record.setRealEstateCertificatePhoto(housePhoto);
        record.setMaritalStatusProofPhoto(maritalPhoto);
        record.setHonestyVideoFile(honestyVideo);
        record.setAssetCertProcessCode(VtAssetCertProcessCodeEnum.REVIEWING);
        record.setRejectReason(null);
        record.setAuditBy(null);
        record.setAuditAt(null);
        if (StrUtil.isBlank(record.getId())) {
            vtAssetCertRecordService.save(record);
        }
        else {
            vtAssetCertRecordService.updateById(record);
        }
        return toDto(record);
    }

    @Override
    public VtAssetCertRecordDTO getLatestByCusCode(String mkUserCode, String cusCode) {
        if (StrUtil.isBlank(cusCode)) {
            return null;
        }
        apiDtMatchmakerService.assertMkUserServesCustomer(mkUserCode, cusCode);
        return toDto(getLatestRecord(cusCode.trim()));
    }

    private VtAssetCertRecord getLatestRecord(String cusCode) {
        return vtAssetCertRecordService.getOne(
                new QueryWrapper<VtAssetCertRecord>().lambda()
                        .eq(VtAssetCertRecord::getCusCode, cusCode)
                        .orderByDesc(VtAssetCertRecord::getCreateTime)
                        .last("LIMIT 1"),
                false);
    }

    private static VtAssetCertRecordDTO toDto(VtAssetCertRecord record) {
        if (record == null) {
            return null;
        }
        return BeanUtil.copyProperties(record, VtAssetCertRecordDTO.class);
    }
}
