package pub.module.verification.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.verification.crud.entity.VtAssetCertRecord;

/**
 * 资产认证记录 Service
 */
public interface VtAssetCertRecordService extends IService<VtAssetCertRecord> {

    VtAssetCertRecord getByCode(String code);
}
