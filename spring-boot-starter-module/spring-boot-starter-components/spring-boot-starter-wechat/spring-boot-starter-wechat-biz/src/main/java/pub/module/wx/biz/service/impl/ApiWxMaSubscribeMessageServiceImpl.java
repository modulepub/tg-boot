package pub.module.wx.biz.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.wx.api.service.ApiWxMaSubscribeMessageService;
import pub.module.wx.biz.config.WxMaConfigResolver;
import pub.module.wx.crud.entity.WxMaSubscribeSendLog;
import pub.module.wx.crud.service.WxMaSubscribeSendLogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApiWxMaSubscribeMessageServiceImpl implements ApiWxMaSubscribeMessageService {

    @Resource
    private WxMaService wxMaService;
    @Resource
    private WxMaConfigResolver wxMaConfigResolver;
    @Resource
    private WxMaSubscribeSendLogService wxMaSubscribeSendLogService;

    @Override
    public SendResult send(SendRequest request) {
        SendResult result = new SendResult();
        if (request == null || StrUtil.isBlank(request.getToOpenId()) || StrUtil.isBlank(request.getTemplateId())) {
            result.setSuccess(false);
            result.setWxErrMsg("openId 或 templateId 为空");
            return result;
        }
        String idempotentKey = StrUtil.trimToNull(request.getIdempotentKey());
        if (idempotentKey != null && wxMaSubscribeSendLogService.existsByIdempotentKey(idempotentKey)) {
            result.setSuccess(true);
            result.setSkipped(true);
            return result;
        }

        String appId = wxMaConfigResolver.resolveAppId(request.getWxMiniConfigCode(), request.getAppId());
        WxMaSubscribeSendLog logRow = new WxMaSubscribeSendLog();
        logRow.setSendLogCode(IdUtil.getSnowflakeNextIdStr());
        logRow.setIdempotentKey(idempotentKey);
        logRow.setToOpenId(request.getToOpenId().trim());
        logRow.setTemplateId(request.getTemplateId().trim());
        logRow.setJumpPage(StrUtil.trimToNull(request.getPage()));
        if (request.getData() != null && !request.getData().isEmpty()) {
            logRow.setSendDataJson(JSONUtil.toJsonStr(request.getData()));
        }

        try {
            wxMaService.switchoverTo(appId);
            WxMaSubscribeMessage message = WxMaSubscribeMessage.builder()
                    .toUser(request.getToOpenId().trim())
                    .templateId(request.getTemplateId().trim())
                    .page(StrUtil.blankToDefault(StrUtil.trim(request.getPage()), ""))
                    .data(buildMsgData(request.getData()))
                    .build();
            wxMaService.getSubscribeService().sendSubscribeMsg(message);
            logRow.setSendStatusCode(StatusCodeEnum.YES);
            wxMaSubscribeSendLogService.save(logRow);
            result.setSuccess(true);
            return result;
        }
        catch (WxErrorException ex) {
            String errCode = String.valueOf(ex.getError().getErrorCode());
            String errMsg = ex.getError().getErrorMsg();
            log.warn("订阅消息发送失败 openId={} templateId={} errCode={} errMsg={}",
                    request.getToOpenId(), request.getTemplateId(), errCode, errMsg);
            logRow.setSendStatusCode(StatusCodeEnum.NO);
            logRow.setWxErrCode(errCode);
            logRow.setWxErrMsg(errMsg);
            wxMaSubscribeSendLogService.save(logRow);
            result.setSuccess(false);
            result.setWxErrCode(errCode);
            result.setWxErrMsg(errMsg);
            return result;
        }
        catch (Exception ex) {
            log.warn("订阅消息发送异常 openId={} templateId={}: {}",
                    request.getToOpenId(), request.getTemplateId(), ex.getMessage());
            logRow.setSendStatusCode(StatusCodeEnum.NO);
            logRow.setWxErrMsg(ex.getMessage());
            wxMaSubscribeSendLogService.save(logRow);
            result.setSuccess(false);
            result.setWxErrMsg(ex.getMessage());
            return result;
        }
    }

    private static List<WxMaSubscribeMessage.MsgData> buildMsgData(Map<String, String> data) {
        List<WxMaSubscribeMessage.MsgData> list = new ArrayList<>();
        if (data == null || data.isEmpty()) {
            return list;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (StrUtil.isBlank(entry.getKey())) {
                continue;
            }
            list.add(new WxMaSubscribeMessage.MsgData(entry.getKey().trim(),
                    StrUtil.nullToEmpty(entry.getValue())));
        }
        return list;
    }
}
