package pub.module.verification.crud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pub.module.verification.crud.entity.CmRecord;

import java.util.Collection;

public interface CmRecordService extends IService<CmRecord> {

    CmRecord getByCode(String cmRecordCode);

    CmRecord getByVendorTraceId(String cmRecordVendorTraceId);

    /**
     * 按内容查找可复用的历史审核记录（同一发起方/用户/内容类型下内容完全一致的最近一条）。
     * <p>用于「内容相同则复用既有审核结果，避免重复送审与重复记录」。</p>
     */
    CmRecord findReusableByContent(String sourceModuleCode, String userCode, String contentTypeCode, String content);

    boolean removeByBizCodes(Collection<String> cmRecordCodes);
}
