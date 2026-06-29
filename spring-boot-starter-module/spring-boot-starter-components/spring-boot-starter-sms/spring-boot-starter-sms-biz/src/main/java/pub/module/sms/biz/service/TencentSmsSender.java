package pub.module.sms.biz.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pub.module.sms.biz.config.TencentConfigRuntimeHolder;
import pub.module.sms.biz.config.TencentConfigRuntimeSnapshot;

import cn.hutool.json.JSONUtil;
import pub.module.sms.biz.exception.SmsSendException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云短信发送（模板短信）。
 */
@Slf4j
@Component
public class TencentSmsSender {

    @Resource
    private TencentConfigRuntimeHolder tencentConfigRuntimeHolder;

    public String sendTemplateSms(String mobile, List<String> mobileList, String templateId,
            List<String> templateParams, String smsProviderCode) {
        Assert.notBlank(templateId, "templateId 不能为空");
        List<String> phones = resolvePhones(mobile, mobileList);
        Assert.notEmpty(phones, "手机号不能为空");

        TencentConfigRuntimeSnapshot snapshot = resolveSnapshot(null);
        Assert.isTrue(snapshot.isReady(), "腾讯云短信配置未就绪，请先在管理端配置并启用 sms_tencent_config");
        Assert.notBlank(snapshot.getSignName(), "短信签名不能为空，请先在 sms_tencent_config 中配置默认签名");

        String requestJson = buildRequestJson(snapshot, templateId, phones, templateParams);
        try {
            Credential cred = new Credential(snapshot.getSecretId(), snapshot.getSecretKey());
            SmsClient client = new SmsClient(cred, snapshot.getRegion());

            SendSmsRequest req = new SendSmsRequest();
            req.setSmsSdkAppId(snapshot.getSdkAppId());
            req.setSignName(snapshot.getSignName());
            req.setTemplateId(templateId);
            req.setPhoneNumberSet(phones.toArray(new String[0]));
            if (templateParams != null && !templateParams.isEmpty()) {
                req.setTemplateParamSet(templateParams.toArray(new String[0]));
            }

            SendSmsResponse resp = client.SendSms(req);
            String responseJson = buildResponseJson(resp);
            SendStatus[] statusSet = resp.getSendStatusSet();
            if (statusSet == null || statusSet.length == 0) {
                throw new SmsSendException("腾讯云短信返回为空", requestJson, responseJson);
            }
            SendStatus first = statusSet[0];
            if (!"Ok".equalsIgnoreCase(first.getCode())) {
                log.error("腾讯云短信发送失败 code={} message={} phones={} templateId={}",
                        first.getCode(), first.getMessage(), phones, templateId);
                throw new SmsSendException(
                        "腾讯云短信发送失败：" + first.getMessage(), requestJson, responseJson);
            }
            log.info("腾讯云短信发送成功 serialNo={} phones={} templateId={}",
                    first.getSerialNo(), phones, templateId);
            Map<String, Object> outcome = new LinkedHashMap<>();
            outcome.put("request", JSONUtil.parseObj(requestJson));
            outcome.put("channelResponse", JSONUtil.parseObj(responseJson));
            return JSONUtil.toJsonStr(outcome);
        }
        catch (TencentCloudSDKException e) {
            log.error("腾讯云短信 SDK 调用失败 phones={} templateId={}", phones, templateId, e);
            throw new SmsSendException("腾讯云短信发送失败：" + e.getMessage(), requestJson, null, e);
        }
    }

    private TencentConfigRuntimeSnapshot resolveSnapshot(String configCode) {
        if (StrUtil.isNotBlank(configCode)) {
            return tencentConfigRuntimeHolder.findByCode(configCode)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "未找到启用的腾讯云短信配置：" + configCode));
        }
        return tencentConfigRuntimeHolder.current();
    }

    private List<String> resolvePhones(String mobile, List<String> mobileList) {
        List<String> phones = new ArrayList<>();
        if (StrUtil.isNotBlank(mobile)) {
            phones.add(normalizePhone(mobile));
        }
        if (mobileList != null) {
            for (String item : mobileList) {
                if (StrUtil.isNotBlank(item)) {
                    phones.add(normalizePhone(item));
                }
            }
        }
        return phones;
    }

    private String buildResponseJson(SendSmsResponse resp) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(resp.getRequestId())) {
            map.put("requestId", resp.getRequestId());
        }
        SendStatus[] statusSet = resp.getSendStatusSet();
        if (statusSet != null && statusSet.length > 0) {
            List<Map<String, Object>> statuses = new ArrayList<>();
            for (SendStatus status : statusSet) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("serialNo", status.getSerialNo());
                item.put("phoneNumber", status.getPhoneNumber());
                item.put("fee", status.getFee());
                item.put("sessionContext", status.getSessionContext());
                item.put("code", status.getCode());
                item.put("message", status.getMessage());
                item.put("isoCode", status.getIsoCode());
                statuses.add(item);
            }
            map.put("sendStatusSet", statuses);
        }
        return JSONUtil.toJsonStr(map);
    }

    private String buildRequestJson(TencentConfigRuntimeSnapshot snapshot, String templateId,
            List<String> phones, List<String> templateParams) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("smsSdkAppId", snapshot.getSdkAppId());
        map.put("signName", snapshot.getSignName());
        map.put("templateId", templateId);
        map.put("phoneNumberSet", phones);
        if (templateParams != null && !templateParams.isEmpty()) {
            map.put("templateParamSet", templateParams);
        }
        map.put("region", snapshot.getRegion());
        return JSONUtil.toJsonStr(map);
    }

    /** 腾讯云要求 E.164 格式，国内号码补 +86 前缀 */
    private String normalizePhone(String mobile) {
        String trimmed = StrUtil.trim(mobile);
        if (trimmed.startsWith("+")) {
            return trimmed;
        }
        if (trimmed.startsWith("86") && trimmed.length() > 11) {
            return "+" + trimmed;
        }
        return "+86" + trimmed;
    }
}
