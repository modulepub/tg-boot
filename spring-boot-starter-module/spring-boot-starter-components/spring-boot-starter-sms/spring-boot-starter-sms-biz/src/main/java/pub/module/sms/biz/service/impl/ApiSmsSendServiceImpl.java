package pub.module.sms.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.sms.api.dto.SendSmsDTO;
import pub.module.sms.api.service.ApiSmsSendService;
import pub.module.sms.biz.exception.SmsSendException;
import pub.module.sms.biz.service.SmsProviderRegistry;
import pub.module.sms.biz.service.SpiSmsService;
import pub.module.sms.crud.entity.SmsSendLog;
import pub.module.sms.crud.entity.SmsTemplate;
import pub.module.sms.crud.service.ISmsSendLogService;
import pub.module.sms.crud.service.ISmsTemplateService;

/**
 * 短信发送 API 实现（按 providerCode 路由至渠道实现，支持 smsTemplateCode 加载模板）。
 */
@Service
public class ApiSmsSendServiceImpl implements ApiSmsSendService {

    @Resource
    private SmsProviderRegistry smsProviderRegistry;
    @Resource
    private ISmsTemplateService smsTemplateService;
    @Resource
    private ISmsSendLogService smsSendLogService;

    @Override
    public String sendSms(SendSmsDTO dto) {
        Assert.notNull(dto, "SendSmsDTO 不能为空");
        String provider = StrUtil.isNotBlank(dto.getSmsProviderCode()) ? dto.getSmsProviderCode() : "tencent";
        Assert.notBlank(provider, "smsProviderCode 不能为空");

        String resolvedTemplateId = null;
        String resolvedContent = null;

        // 如果传了 smsTemplateCode，则从 sms_template 加载 templateCode（映射到渠道 templateId）
        if (StrUtil.isNotBlank(dto.getSmsTemplateCode())) {
            SmsTemplate tpl = smsTemplateService.getByCode(dto.getSmsTemplateCode());
            Assert.notNull(tpl, "短信模板不存在：" + dto.getSmsTemplateCode());
            Assert.isTrue("1".equals(tpl.getSmsTemplateEnabledCode()), "短信模板未启用：" + dto.getSmsTemplateCode());
            resolvedTemplateId = tpl.getSmsTemplateId();
            resolvedContent = tpl.getSmsTemplateContent();
        }

        SpiSmsService spiSmsService = smsProviderRegistry.require(provider);

        // 构建 SPI 入参（内部字段 templateId 由渠道方使用，映射自 sms_template.template_code）
        SpiSmsService.SpiSendSmsDTO spiDto = SpiSmsService.SpiSendSmsDTO.builder()
                .mobile(dto.getMobile())
                .mobileList(dto.getMobileList())
                .content(resolvedContent)
                .templateId(resolvedTemplateId)
                .templateParams(dto.getTemplateParams())
                .smsProviderCode(provider)
                .build();

        String requestSnapshot = JSONUtil.toJsonStr(spiDto);

        SmsSendLog sendLog = new SmsSendLog();
        sendLog.setSmsProviderCode(provider);
        sendLog.setSmsTemplateCode(dto.getSmsTemplateCode());
        sendLog.setMobile(StrUtil.isNotBlank(dto.getMobile()) ? dto.getMobile()
                : (dto.getMobileList() != null ? String.join(",", dto.getMobileList()) : ""));
        sendLog.setTemplateParams(dto.getTemplateParams() != null ? JSONUtil.toJsonStr(dto.getTemplateParams()) : null);
        sendLog.setProviderRequestJson(requestSnapshot);
        smsSendLogService.save(sendLog);

        try {
            String channelResult = spiSmsService.spiSendSms(spiDto);
            sendLog.setSuccessCode("1");
            applyChannelResult(sendLog, channelResult, requestSnapshot);
            smsSendLogService.updateById(sendLog);
            return channelResult;
        } catch (SmsSendException e) {
            sendLog.setSuccessCode("0");
            if (StrUtil.isNotBlank(e.getRequestJson())) {
                sendLog.setProviderRequestJson(e.getRequestJson());
            }
            sendLog.setProviderResponseJson(e.getResponseJson());
            sendLog.setErrorMessage(e.getMessage());
            smsSendLogService.updateById(sendLog);
            throw e;
        } catch (Exception e) {
            sendLog.setSuccessCode("0");
            sendLog.setErrorMessage(e.getMessage());
            smsSendLogService.updateById(sendLog);
            throw e;
        }
    }

    /** 解析渠道返回：腾讯云成功时为 {request, channelResponse}，其余渠道为纯文本或 JSON。 */
    private void applyChannelResult(SmsSendLog sendLog, String channelResult, String requestSnapshot) {
        if (StrUtil.isBlank(channelResult)) {
            return;
        }
        if (JSONUtil.isTypeJSON(channelResult)) {
            var obj = JSONUtil.parseObj(channelResult);
            if (obj.containsKey("request") && obj.containsKey("channelResponse")) {
                sendLog.setProviderRequestJson(obj.getJSONObject("request").toString());
                sendLog.setProviderResponseJson(obj.getJSONObject("channelResponse").toString());
                return;
            }
        }
        sendLog.setProviderResponseJson(channelResult);
        if (StrUtil.isBlank(sendLog.getProviderRequestJson())) {
            sendLog.setProviderRequestJson(requestSnapshot);
        }
    }
}
