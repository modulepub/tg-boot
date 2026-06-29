package pub.module.dating.biz.support;

import cn.hutool.core.util.StrUtil;
import pub.module.verification.api.constants.ContentModerationTypeCodeEnum;
import pub.module.verification.api.dto.ContentModerationItemDTO;
import pub.module.verification.api.util.MediaUrlClassifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户资料编辑需走内容审核的字段定义与检测项组装。
 */
public final class CustomerProfileEditModerationSupport {

    public static final String SOURCE_MODULE = "dating_profile_edit";
    public static final String BIZ_CODE_SEPARATOR = "|";

    private static final Set<String> TEXT_FIELDS = Set.of(
            "cusNickName", "cusName", "cusOccupationalDescription", "cusDesc", "cusRemark",
            "cusDemand", "cusMoment", "cusWechatId", "cusWxIdNo", "cusEducationName", "cusCityResidenceName");

    private static final Set<String> MEDIA_FIELDS = Set.of(
            "cusAvatar", "cusTeenagePhoto", "cusLifePhoto",
            "cusAnnualIncomeAuthenticatedPhoto", "cusVehicleLicensePhoto", "cusRealEstateCertificatePhoto");

    private CustomerProfileEditModerationSupport() {
    }

    public static boolean requiresModeration(String fieldName) {
        if (StrUtil.isBlank(fieldName)) {
            return false;
        }
        return TEXT_FIELDS.contains(fieldName) || MEDIA_FIELDS.contains(fieldName);
    }

    public static String buildBizCode(String ownerCode, String fieldName, Integer fieldItemIndex) {
        String base = StrUtil.trim(ownerCode) + BIZ_CODE_SEPARATOR + StrUtil.trim(fieldName);
        if (fieldItemIndex == null) {
            return base;
        }
        return base + BIZ_CODE_SEPARATOR + fieldItemIndex;
    }

    public static List<ContentModerationItemDTO> buildModerationItems(String fieldName, Object rawValue) {
        if (rawValue == null) {
            return List.of();
        }
        String field = StrUtil.trim(fieldName);
        if (TEXT_FIELDS.contains(field)) {
            String text = StrUtil.trim(String.valueOf(rawValue));
            if (StrUtil.isBlank(text)) {
                return List.of();
            }
            ContentModerationItemDTO item = new ContentModerationItemDTO();
            item.setCmRecordContentTypeCode(ContentModerationTypeCodeEnum.TEXT.getCode());
            item.setCmRecordContent(text);
            return List.of(item);
        }
        if (MEDIA_FIELDS.contains(field)) {
            return buildMediaItems(String.valueOf(rawValue));
        }
        return List.of();
    }

    private static List<ContentModerationItemDTO> buildMediaItems(String raw) {
        Set<String> urls = new LinkedHashSet<>();
        for (String part : StrUtil.nullToEmpty(raw).split(",")) {
            String trimmed = StrUtil.trim(part);
            if (StrUtil.isNotBlank(trimmed)) {
                urls.add(trimmed);
            }
        }
        List<ContentModerationItemDTO> items = new ArrayList<>();
        for (String url : urls) {
            ContentModerationItemDTO item = new ContentModerationItemDTO();
            item.setCmRecordContentTypeCode(MediaUrlClassifier.classifyUrl(url));
            item.setCmRecordContent(url);
            items.add(item);
        }
        return items;
    }
}
