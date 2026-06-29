package pub.module.verification.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.verification.api.constants.CmRecordProcessCodeEnum;
import pub.module.verification.api.constants.ContentModerationPluginCodeEnum;
import pub.module.verification.api.constants.ContentModerationTypeCodeEnum;
import pub.module.verification.api.dto.ContentModerationPluginOutcome;
import pub.module.verification.api.dto.ContentModerationRequest;
import pub.module.verification.biz.service.SpiContentModerationPlugin;
import pub.module.wx.api.dto.WxMaMediaCheckAsyncRequest;
import pub.module.wx.api.dto.WxMaMediaCheckAsyncResult;
import pub.module.wx.api.dto.WxMaMsgSecCheckRequest;
import pub.module.wx.api.dto.WxMaMsgSecCheckResult;
import pub.module.wx.api.service.ApiWxMaContentSecurityService;

@Slf4j
@Service
public class WechatSpiContentModerationPlugin implements SpiContentModerationPlugin {

    private static final int DEFAULT_SCENE = 1;

    @Resource
    private ApiWxMaContentSecurityService apiWxMaContentSecurityService;
    @Resource
    private ApiSysUserService apiSysUserService;

    @Override
    public String pluginCode() {
        return ContentModerationPluginCodeEnum.WECHAT_MEDIA_CHECK.getCode();
    }

    @Override
    public ContentModerationPluginOutcome check(ContentModerationRequest context, String contentTypeCode, String content) {
        String openId = resolveOpenId(context.getCmRecordUserCode());
        if (StrUtil.isBlank(openId)) {
            return unreachableOutcome("用户需通过微信小程序登录并绑定 openId 后再提交内容审核");
        }
        int scene = context.getWxSecCheckScene() != null ? context.getWxSecCheckScene() : DEFAULT_SCENE;
        String appId = StrUtil.trim(context.getWxMaAppId());

        if (ContentModerationTypeCodeEnum.TEXT.getCode().equalsIgnoreCase(contentTypeCode)) {
            return checkText(appId, openId, scene, content);
        }
        return checkMedia(appId, openId, scene, contentTypeCode, content);
    }

    private ContentModerationPluginOutcome checkText(String appId, String openId, int scene, String content) {
        WxMaMsgSecCheckRequest req = new WxMaMsgSecCheckRequest();
        req.setWxMaAppId(appId);
        req.setOpenId(openId);
        req.setScene(scene);
        req.setContent(content);
        WxMaMsgSecCheckResult resp = apiWxMaContentSecurityService.msgSecCheck(req);
        if (!resp.isApiReachable()) {
            return unreachableOutcome(StrUtil.blankToDefault(resp.getErrMsg(), "微信文本检测调用失败"));
        }
        String suggest = StrUtil.blankToDefault(resp.getSuggest(), "pass");
        boolean pass = !"risky".equalsIgnoreCase(suggest);
        ContentModerationPluginOutcome.ContentModerationPluginOutcomeBuilder builder = ContentModerationPluginOutcome.builder()
                .apiReachable(true)
                .async(false)
                .cmRecordPluginCode(pluginCode())
                .cmRecordProcessCode(CmRecordProcessCodeEnum.FINISHED.getCode())
                .cmRecordPassedStatusCode(pass ? StatusCodeEnum.YES.getCode() : StatusCodeEnum.NO.getCode())
                .cmRecordVendorTraceId(resp.getTraceId())
                .cmRecordRemark(resp.getRawSummary());
        if (!pass) {
            builder.cmRecordNotPassedReason(buildTextNotPassedReason(suggest, resp.getLabel()));
        }
        return builder.build();
    }

    private ContentModerationPluginOutcome checkMedia(String appId, String openId, int scene,
                                                      String contentTypeCode, String mediaUrl) {
        WxMaMediaCheckAsyncRequest req = new WxMaMediaCheckAsyncRequest();
        req.setWxMaAppId(appId);
        req.setOpenId(openId);
        req.setScene(scene);
        req.setMediaUrl(mediaUrl);
        req.setMediaType(resolveMediaType(contentTypeCode));
        WxMaMediaCheckAsyncResult resp = apiWxMaContentSecurityService.mediaCheckAsync(req);
        if (!resp.isApiReachable()) {
            return unreachableOutcome(StrUtil.blankToDefault(resp.getErrMsg(), "微信媒体检测提交失败"));
        }
        return ContentModerationPluginOutcome.builder()
                .apiReachable(true)
                .async(true)
                .cmRecordPluginCode(pluginCode())
                .cmRecordProcessCode(CmRecordProcessCodeEnum.REVIEWING.getCode())
                .cmRecordVendorTraceId(resp.getTraceId())
                .cmRecordRemark(resp.getRawSummary())
                .build();
    }

    private static int resolveMediaType(String contentTypeCode) {
        if (ContentModerationTypeCodeEnum.VIDEO.getCode().equalsIgnoreCase(contentTypeCode)) {
            return 1;
        }
        return 2;
    }

    private String resolveOpenId(String userCode) {
        if (StrUtil.isBlank(userCode)) {
            return null;
        }
        UserDTO user = apiSysUserService.getUserByUserCode(userCode.trim());
        return user != null ? StrUtil.trim(user.getUserWxOpenId()) : null;
    }

    private static ContentModerationPluginOutcome unreachableOutcome(String message) {
        return ContentModerationPluginOutcome.builder()
                .apiReachable(false)
                .async(false)
                .cmRecordPluginCode(ContentModerationPluginCodeEnum.WECHAT_MEDIA_CHECK.getCode())
                .cmRecordRemark(message)
                .build();
    }

    private static String buildTextNotPassedReason(String suggest, Integer label) {
        if (label != null) {
            return "微信文本检测未通过，suggest=" + suggest + "，label=" + label;
        }
        return "微信文本检测未通过，suggest=" + suggest;
    }
}
