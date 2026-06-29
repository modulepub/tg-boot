package pub.module.dating.biz.messaging;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import pub.module.dating.biz.service.impl.ApiDtCustomerProfileEditServiceImpl;
import pub.module.dating.biz.support.CustomerProfileEditModerationSupport;
import pub.module.verification.api.dto.ContentModerationRecordFinishedDTO;
import pub.module.verification.api.service.ContentModerationRecordFinishedListener;

/**
 * 资料编辑：内容审核异步/人工结束后同步通过字段到 customer。
 */
@Component
public class DtCustomerProfileEditCmRecordListener implements ContentModerationRecordFinishedListener {

    @Resource
    private ApiDtCustomerProfileEditServiceImpl apiDtCustomerProfileEditServiceImpl;

    @Override
    public void onRecordFinished(ContentModerationRecordFinishedDTO event) {
        if (event == null) {
            return;
        }
        if (!CustomerProfileEditModerationSupport.SOURCE_MODULE.equals(event.getCmRecordSourceModuleCode())) {
            return;
        }
        apiDtCustomerProfileEditServiceImpl.handleCmRecordFinished(event);
    }
}
