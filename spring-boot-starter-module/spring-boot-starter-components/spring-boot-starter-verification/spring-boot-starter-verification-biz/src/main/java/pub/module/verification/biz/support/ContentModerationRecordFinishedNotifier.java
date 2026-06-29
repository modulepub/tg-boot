package pub.module.verification.biz.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import pub.module.verification.api.dto.ContentModerationRecordFinishedDTO;
import pub.module.verification.api.service.ContentModerationRecordFinishedListener;
import pub.module.verification.crud.entity.CmRecord;

@Slf4j
@Component
public class ContentModerationRecordFinishedNotifier {

    private final ObjectProvider<ContentModerationRecordFinishedListener> listeners;

    public ContentModerationRecordFinishedNotifier(
            ObjectProvider<ContentModerationRecordFinishedListener> listeners) {
        this.listeners = listeners;
    }

    public void notifyFinished(CmRecord row) {
        if (row == null) {
            return;
        }
        ContentModerationRecordFinishedDTO event = ContentModerationRecordFinishedDTO.builder()
                .cmRecordCode(row.getCmRecordCode())
                .cmRecordSourceModuleCode(row.getCmRecordSourceModuleCode())
                .cmRecordBizCode(row.getCmRecordBizCode())
                .cmRecordContentTypeCode(row.getCmRecordContentTypeCode())
                .cmRecordProcessCode(row.getCmRecordProcessCode() != null ? row.getCmRecordProcessCode().getCode() : null)
                .cmRecordPassedStatusCode(row.getCmRecordPassedStatusCode())
                .cmRecordNotPassedReason(row.getCmRecordNotPassedReason())
                .build();
        listeners.orderedStream().forEach(listener -> {
            try {
                listener.onRecordFinished(event);
            } catch (Exception ex) {
                log.warn("内容审核结束回调失败 listener={} cmRecordCode={}",
                        listener.getClass().getSimpleName(), row.getCmRecordCode(), ex);
            }
        });
    }
}
