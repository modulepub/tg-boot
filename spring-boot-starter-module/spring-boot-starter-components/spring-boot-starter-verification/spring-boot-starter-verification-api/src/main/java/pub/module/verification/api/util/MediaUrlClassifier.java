package pub.module.verification.api.util;

import cn.hutool.core.util.StrUtil;
import pub.module.verification.api.constants.ContentModerationTypeCodeEnum;

import java.util.Locale;
import java.util.Set;

/**
 * 根据 URL 后缀推断媒体类型
 */
public final class MediaUrlClassifier {

    private static final Set<String> IMAGE_EXT = Set.of(
            "jpg", "jpeg", "png", "bmp", "gif", "webp");
    private static final Set<String> VIDEO_EXT = Set.of(
            "mp4", "mov", "webm", "avi", "mkv", "m4v", "flv", "wmv", "3gp");

    private MediaUrlClassifier() {
    }

    public static String classifyUrl(String url) {
        String ext = extractExtension(url);
        if (IMAGE_EXT.contains(ext)) {
            return ContentModerationTypeCodeEnum.IMAGE.getCode();
        }
        if (VIDEO_EXT.contains(ext)) {
            return ContentModerationTypeCodeEnum.VIDEO.getCode();
        }
        return ContentModerationTypeCodeEnum.IMAGE.getCode();
    }

    private static String extractExtension(String url) {
        if (StrUtil.isBlank(url)) {
            return "";
        }
        String path = url.trim();
        int queryIdx = path.indexOf('?');
        if (queryIdx >= 0) {
            path = path.substring(0, queryIdx);
        }
        int slash = path.lastIndexOf('/');
        if (slash >= 0 && slash < path.length() - 1) {
            path = path.substring(slash + 1);
        }
        int dot = path.lastIndexOf('.');
        if (dot < 0 || dot == path.length() - 1) {
            return "";
        }
        return path.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
