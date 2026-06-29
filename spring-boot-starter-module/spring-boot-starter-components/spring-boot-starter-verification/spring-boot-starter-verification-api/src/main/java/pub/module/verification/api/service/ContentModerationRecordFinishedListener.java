package pub.module.verification.api.service;

import pub.module.verification.api.dto.ContentModerationRecordFinishedDTO;

/**
 * 内容审核记录流程结束后的扩展回调（跨模块实现，如资料编辑同步）。
 */
public interface ContentModerationRecordFinishedListener {

    void onRecordFinished(ContentModerationRecordFinishedDTO event);
}
