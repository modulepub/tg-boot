package pub.module.verification.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.cloudauth20190307.Client;
import com.aliyun.cloudauth20190307.models.Mobile2MetaVerifyRequest;
import com.aliyun.cloudauth20190307.models.Mobile2MetaVerifyResponse;
import com.aliyun.cloudauth20190307.models.Mobile2MetaVerifyResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.models.Config;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pub.module.verification.api.dto.PhoneTwoFactorChannelOutcome;
import pub.module.verification.biz.service.SpiPhoneTwoFactorChannel;
import pub.module.verification.biz.config.NpConfigRuntimeHolder;
import pub.module.verification.biz.config.NpConfigRuntimeSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 阿里云实人认证 · 信息核验 · 手机号二要素（Mobile2MetaVerify）。
 * <p>对应控制台：云盾 / 信息核验 / N要素 / 手机号二要素。仅需 AccessKey，无 AuthCode。</p>
 * <p>业务结果以 {@code ResultObject.BizCode} 为准：1 一致、2 不一致、3 查无；{@code Code=200} 仅表示接口调用成功。</p>
 */
@Slf4j
@Service
@Primary
public class AliyunSpiPhoneTwoFactorChannel implements SpiPhoneTwoFactorChannel {

    public static final String PROVIDER_CODE = NpConfigRuntimeSnapshot.PROVIDER_ALIYUN_CLOUDAUTH;

    /** 本系统落库：1=核验通过，0=未通过（含 BizCode 2/3） */
    public static final String PASSED_YES = "1";
    public static final String PASSED_NO = "0";

    @Resource
    private NpConfigRuntimeHolder npConfigRuntimeHolder;

    @Override
    public String getProviderCode() {
        NpConfigRuntimeSnapshot cfg = npConfigRuntimeHolder.current();
        return StrUtil.blankToDefault(cfg.getProviderCode(), PROVIDER_CODE);
    }

    @Override
    public PhoneTwoFactorChannelOutcome verify(String phone, String realName) {
        NpConfigRuntimeSnapshot cfg = npConfigRuntimeHolder.current();
        if (!cfg.isAliyunReady()) {
            return PhoneTwoFactorChannelOutcome.builder()
                    .providerCode(PROVIDER_CODE)
                    .apiReachable(false)
                    .vendorMessage("二要素未启用或 vt_np_config 中 AccessKey 未配置，请在管理端维护并刷新运行时配置")
                    .build();
        }
        try {
            Client client = new Client(buildConfig(cfg));
            Mobile2MetaVerifyRequest req = new Mobile2MetaVerifyRequest();
            req.setParamType(mapParamType(cfg.getMask()));
            req.setMobile(phone);
            req.setUserName(realName);
            log.info("Mobile2MetaVerify 请求 paramType={} mobile={} userName={}",
                    req.getParamType(), maskPhone(phone), maskName(realName));
            Mobile2MetaVerifyResponse resp = client.mobile2MetaVerify(req);
            log.info("Mobile2MetaVerify 响应 statusCode={} body={}",
                    resp == null ? null : resp.getStatusCode(), formatResponseBody(resp));
            return parseResponse(resp);
        }
        catch (Exception e) {
            logAliyunException(e);
            String friendly = toFriendlyVendorMessage(e);
            log.error("阿里云 Mobile2MetaVerify 调用失败: {}", friendly);
            return PhoneTwoFactorChannelOutcome.builder()
                    .providerCode(PROVIDER_CODE)
                    .apiReachable(false)
                    .vendorMessage(StrUtil.sub(friendly, 0, 500))
                    .rawSummary(StrUtil.sub(friendly, 0, 2000))
                    .build();
        }
    }

    private static Config buildConfig(NpConfigRuntimeSnapshot cfg) {
        Config config = new Config();
        config.setAccessKeyId(cfg.getAccessKeyId());
        config.setAccessKeySecret(cfg.getAccessKeySecret());
        config.setEndpoint(StrUtil.blankToDefault(cfg.getEndpoint(), "cloudauth.aliyuncs.com"));
        return config;
    }

    private static String toFriendlyVendorMessage(Exception e) {
        String msg = StrUtil.nullToEmpty(e.getMessage());
        if (msg.contains("access key is not found") || msg.contains("InvalidAccessKeyId.NotFound")) {
            return "AccessKeyId 在阿里云不存在或已删除，请到 RAM 控制台核对（须为 LTAI 开头的 AccessKey，勿填账号 UID）。原始信息: " + msg;
        }
        if (msg.contains("SignatureDoesNotMatch")) {
            return "AccessKeySecret 与 AccessKeyId 不匹配，请重新复制 RAM 中的密钥对。原始信息: " + msg;
        }
        return msg;
    }

    private static String mapParamType(String mask) {
        if ("MD5".equalsIgnoreCase(StrUtil.trim(mask))) {
            return "md5";
        }
        return "normal";
    }

    private PhoneTwoFactorChannelOutcome parseResponse(Mobile2MetaVerifyResponse resp) {
        if (resp == null || resp.getBody() == null) {
            return PhoneTwoFactorChannelOutcome.builder()
                    .providerCode(PROVIDER_CODE)
                    .apiReachable(false)
                    .vendorMessage("上游返回为空")
                    .build();
        }
        Mobile2MetaVerifyResponseBody body = resp.getBody();
        String apiCode = normalizeDigits(body.getCode());
        var b = PhoneTwoFactorChannelOutcome.builder()
                .providerCode(PROVIDER_CODE)
                .apiReachable(true)
                .vendorCode(apiCode)
                .vendorRequestId(body.getRequestId())
                .rawSummary(summarizeBody(body));
        if (!isAliyunApiInvokeSuccess(apiCode)) {
            b.vendorMessage(StrUtil.blankToDefault(body.getMessage(), "接口调用失败"));
            return b.build();
        }
        String bizCode = extractBizCode(body);
        b.vendorBizCode(bizCode);
        String passedStatus = mapBizCodeToPassedStatus(bizCode);
        b.isConsistentCode(passedStatus);
        b.vendorMessage(messageForBizCode(bizCode));
        if (body.getResultObject() != null) {
            b.basicCarrier(body.getResultObject().getIspName());
        }
        String verifyMessage = messageForBizCode(bizCode);
        log.info("Mobile2MetaVerify 解析: apiCode={} bizCode={} -> passedStatus={} verifyMessage={}",
                apiCode, bizCode, passedStatus, verifyMessage);
        return b.build();
    }

    /** 接口 Code=200 仅表示调用成功，不代表 BizCode 核验通过 */
    private static boolean isAliyunApiInvokeSuccess(String apiCode) {
        return "200".equals(apiCode) || "OK".equalsIgnoreCase(apiCode);
    }

    /**
     * 按阿里云文档 BizCode 映射为本系统是否通过：仅 1 为通过。
     */
    static String mapBizCodeToPassedStatus(String bizCode) {
        return PASSED_YES.equals(normalizeDigits(bizCode)) ? PASSED_YES : PASSED_NO;
    }

    /**
     * 面向用户/前端的核验说明（勿使用接口 Message 里的 success）。
     */
    static String messageForBizCode(String bizCode) {
        return switch (normalizeDigits(bizCode)) {
            case "1" -> "核验一致";
            case "2" -> "姓名与手机号不一致";
            case "3" -> "查无记录，未找到该号码对应实名信息";
            default -> StrUtil.isBlank(bizCode) ? "未返回核验结果" : "核验未通过";
        };
    }

    static String extractBizCode(Mobile2MetaVerifyResponseBody body) {
        String fromGetter = "";
        if (body.getResultObject() != null && body.getResultObject().getBizCode() != null) {
            fromGetter = normalizeDigits(body.getResultObject().getBizCode());
        }
        if (StrUtil.isNotBlank(fromGetter)) {
            return fromGetter;
        }
        try {
            Map<String, Object> map = ((TeaModel) body).toMap();
            Object ro = map.get("ResultObject");
            if (ro == null) {
                ro = map.get("resultObject");
            }
            if (ro instanceof Map<?, ?> roMap) {
                Object biz = roMap.get("BizCode");
                if (biz == null) {
                    biz = roMap.get("bizCode");
                }
                return normalizeDigits(biz);
            }
        }
        catch (Exception ignored) {
            // fall through
        }
        try {
            JSONObject json = JSONUtil.parseObj(JSONUtil.toJsonStr(body));
            JSONObject resultObject = json.getJSONObject("ResultObject");
            if (resultObject == null) {
                resultObject = json.getJSONObject("resultObject");
            }
            if (resultObject != null) {
                Object biz = resultObject.get("BizCode");
                if (biz == null) {
                    biz = resultObject.get("bizCode");
                }
                return normalizeDigits(biz);
            }
        }
        catch (Exception ignored) {
            // ignore
        }
        return "";
    }

    private static String normalizeDigits(Object raw) {
        if (raw == null) {
            return "";
        }
        String s = StrUtil.trim(String.valueOf(raw));
        if (StrUtil.isBlank(s)) {
            return "";
        }
        if (s.matches("\\d+")) {
            return String.valueOf(Integer.parseInt(s));
        }
        return s;
    }

    private static String summarizeBody(Mobile2MetaVerifyResponseBody body) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("Code", body.getCode());
        m.put("Message", body.getMessage());
        m.put("RequestId", body.getRequestId());
        String bizCode = extractBizCode(body);
        if (StrUtil.isNotBlank(bizCode)) {
            m.put("BizCode", bizCode);
        }
        if (body.getResultObject() != null) {
            m.put("IspName", body.getResultObject().getIspName());
        }
        return StrUtil.sub(JSONUtil.toJsonStr(m), 0, 2000);
    }

    private static String formatResponseBody(Mobile2MetaVerifyResponse resp) {
        if (resp == null) {
            return "null";
        }
        if (resp.getBody() == null) {
            return "body=null";
        }
        return JSONUtil.toJsonStr(resp.getBody());
    }

    private static void logAliyunException(Exception e) {
        if (e instanceof TeaException tea) {
            log.error("Mobile2MetaVerify TeaException code={} message={} data={}",
                    tea.getCode(), tea.getMessage(), JSONUtil.toJsonStr(tea.getData()));
            return;
        }
        log.error("Mobile2MetaVerify 异常: {}", e.getMessage(), e);
    }

    private static String maskPhone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() < 7) {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private static String maskName(String name) {
        if (StrUtil.isBlank(name)) {
            return "***";
        }
        if (name.length() == 1) {
            return "*";
        }
        return name.charAt(0) + "**";
    }
}
