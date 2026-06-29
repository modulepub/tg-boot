package pub.module.verification.api.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import pub.module.verification.api.dto.ContentModerationAsyncCallbackDTO;
import pub.module.verification.api.dto.ContentModerationBatchResult;
import pub.module.verification.api.dto.ContentModerationRequest;

/**
 * 内容合法校验（文字、图片链接、视频链接），供各业务模块依赖 *-api 调用。
 */
@Validated
public interface ApiContentModerationService {

    /**
     * 按插件批量检测并写入 {@code vt_cm_record}
     */
    ContentModerationBatchResult moderate(@NotNull @Valid ContentModerationRequest request);

    /**
     * 异步检测回调落库（如微信 mediaCheckAsync 的 wxa_media_check 事件）
     */
    void completeAsyncByTraceId(@NotNull @Valid ContentModerationAsyncCallbackDTO callback);
}
