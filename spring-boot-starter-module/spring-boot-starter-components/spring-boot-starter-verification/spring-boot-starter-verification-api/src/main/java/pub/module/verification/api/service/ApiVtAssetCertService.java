package pub.module.verification.api.service;

import pub.module.verification.api.dto.VtAssetCertRecordDTO;
import pub.module.verification.api.dto.VtAssetCertSubmitVO;

/**
 * 用户端-资产认证（爱与诚辅助认证）
 */
public interface ApiVtAssetCertService {

    /** 红娘提交客户资产认证申请 */
    VtAssetCertRecordDTO submitApply(String mkUserCode, VtAssetCertSubmitVO vo);

    /** 查询指定客户最新一条申请（红娘工作台） */
    VtAssetCertRecordDTO getLatestByCusCode(String mkUserCode, String cusCode);
}
