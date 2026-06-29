package pub.module.dating.biz.controller.pub;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtGuestPreviewRecommendDTO;
import pub.module.dating.api.service.dto.DtIntentionDTO;

import java.util.List;

/**
 * 公开-对象推荐（免登录预览等）
 */
@Tag(name = "公开-对象推荐")
@RestController
@RequestMapping("/pub/dating/dtRecommended")
@Slf4j
public class PubDtRecommendedController {

    private static final int GUEST_PREVIEW_LIMIT = 1;

    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Operation(summary = "公开-免登录推荐预览（兼容旧版：仅按性别，最多1条）")
    @GetMapping(value = "/guestPreviewList")
    public Result<List<DtCustomerDTO>> guestPreviewListBySex(
            @RequestParam(name = "intentionSexCode") String intentionSexCode) {
        if (StrUtil.isBlank(intentionSexCode)) {
            return Result.error("intentionSexCode不能为空");
        }
        List<DtCustomerDTO> list = apiDtCustomerService.listGuestPreviewBySexCode(
                intentionSexCode.trim(), GUEST_PREVIEW_LIMIT);
        return Result.ok(list);
    }

    @Operation(summary = "公开-免登录推荐预览（按意向匹配度排序，最多1条）")
    @PostMapping(value = "/guestPreviewList")
    public Result<List<DtGuestPreviewRecommendDTO>> guestPreviewListByIntention(
            @RequestBody DtIntentionDTO intention) {
        if (intention == null || intention.getIntentionSexCode() == null) {
            return Result.error("intentionSexCode不能为空");
        }
        List<DtGuestPreviewRecommendDTO> list = apiDtCustomerService.listGuestPreviewByIntention(
                intention, GUEST_PREVIEW_LIMIT);
        return Result.ok(list);
    }
}
