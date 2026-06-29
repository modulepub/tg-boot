package pub.module.dating.biz.support;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.verification.api.constants.ContentModerationPluginCodeEnum;
import pub.module.verification.api.constants.ContentModerationTypeCodeEnum;
import pub.module.verification.api.dto.ContentModerationBatchResult;
import pub.module.verification.api.dto.ContentModerationItemDTO;
import pub.module.verification.api.dto.ContentModerationRequest;
import pub.module.verification.api.service.ApiContentModerationService;
import pub.module.verification.api.util.MediaUrlClassifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户资料 patch 内容合法校验（编辑客户信息时使用）
 */
@Component
public class DtCustomerContentModerationSupport {

    private static final String SOURCE_MODULE = "dating";

    @Resource
    private ApiContentModerationService apiContentModerationService;

    public void moderatePatch(String userCode, String cusCode, String cusName, DtCustomerDTO patchDto) {
        if (patchDto == null) {
            return;
        }
        ContentModerationRequest request = buildRequest(userCode, cusCode, cusName, patchDto);
        if (request.getItems().isEmpty()) {
            return;
        }
        ContentModerationBatchResult result = apiContentModerationService.moderate(request);
        if (!result.isPassed()) {
            throw new IllegalArgumentException(StrUtil.blankToDefault(
                    result.getBlockedMessage(), "客户资料内容未通过合法校验"));
        }
    }

    private ContentModerationRequest buildRequest(String userCode, String cusCode, String cusName, DtCustomerDTO patchDto) {
        ContentModerationRequest request = new ContentModerationRequest();
        request.setCmRecordPluginCode(ContentModerationPluginCodeEnum.WECHAT_MEDIA_CHECK.getCode());
        request.setCmRecordSourceModuleCode(SOURCE_MODULE);
        request.setCmRecordBizCode(StrUtil.trim(cusCode));
        request.setCmRecordUserCode(StrUtil.trim(userCode));
        request.setCmRecordUserName(StrUtil.blankToDefault(StrUtil.trim(patchDto.getCusName()), StrUtil.trim(cusName)));
        request.setWxSecCheckScene(1);

        List<ContentModerationItemDTO> items = new ArrayList<>();
        appendTextItem(items, patchDto);
        appendMediaItems(items, patchDto);
        request.setItems(items);
        return request;
    }

    private static void appendTextItem(List<ContentModerationItemDTO> items, DtCustomerDTO patchDto) {
        List<String> parts = new ArrayList<>();
        addTextPart(parts, patchDto.getCusNickName());
        addTextPart(parts, patchDto.getCusName());
        addTextPart(parts, patchDto.getCusOccupationalDescription());
        addTextPart(parts, patchDto.getCusDesc());
        addTextPart(parts, patchDto.getCusRemark());
        addTextPart(parts, patchDto.getCusDemand());
        addTextPart(parts, patchDto.getCusMoment());
        addTextPart(parts, patchDto.getCusWechatId());
        addTextPart(parts, patchDto.getCusWxIdNo());
        addTextPart(parts, patchDto.getCusEducationName());
        addTextPart(parts, patchDto.getCusCityResidenceName());
        if (parts.isEmpty()) {
            return;
        }
        ContentModerationItemDTO item = new ContentModerationItemDTO();
        item.setCmRecordContentTypeCode(ContentModerationTypeCodeEnum.TEXT.getCode());
        item.setCmRecordContent(String.join(",", parts));
        items.add(item);
    }

    private static void appendMediaItems(List<ContentModerationItemDTO> items, DtCustomerDTO patchDto) {
        Set<String> urls = new LinkedHashSet<>();
        collectUrl(urls, patchDto.getCusAvatar());
        collectUrl(urls, patchDto.getCusTeenagePhoto());
        collectUrls(urls, patchDto.getCusLifePhoto());
        for (String url : urls) {
            ContentModerationItemDTO item = new ContentModerationItemDTO();
            String typeCode = MediaUrlClassifier.classifyUrl(url);
            item.setCmRecordContentTypeCode(typeCode);
            item.setCmRecordContent(url);
            items.add(item);
        }
    }

    private static void addTextPart(List<String> parts, String value) {
        if (value == null) {
            return;
        }
        String trimmed = StrUtil.trim(value);
        if (StrUtil.isNotBlank(trimmed)) {
            parts.add(trimmed);
        }
    }

    private static void collectUrl(Set<String> urls, String value) {
        if (value == null) {
            return;
        }
        String trimmed = StrUtil.trim(value);
        if (StrUtil.isNotBlank(trimmed)) {
            urls.add(trimmed);
        }
    }

    private static void collectUrls(Set<String> urls, String commaSeparated) {
        if (commaSeparated == null) {
            return;
        }
        for (String part : commaSeparated.split(",")) {
            collectUrl(urls, part);
        }
    }
}
