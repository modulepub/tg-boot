package pub.module.verification.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.verification.api.dto.ContentModerationBatchResult;
import pub.module.verification.api.dto.ContentModerationRequest;
import pub.module.verification.api.service.ApiContentModerationService;

/**
 * 内容合法校验 HTTP 入口（供各模块或管理端直接调用）
 */
@Tag(name = "工具-核验", description = "内容合法校验")
@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class ContentModerationController {

    private final ApiContentModerationService apiContentModerationService;

    @Operation(summary = "内容合法校验（文字/图片链接/视频链接）")
    @PostMapping("/content-moderation")
    public Result<ContentModerationBatchResult> moderate(@Valid @RequestBody ContentModerationRequest request) {
        return Result.ok(apiContentModerationService.moderate(request));
    }
}
