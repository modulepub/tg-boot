package pub.module.finance.biz.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import pub.module.finance.api.constants.FcAcLogNotifyStatusCodeEnum;
import pub.module.finance.api.service.BizFcAccountLogService;
import pub.module.finance.api.service.BizPayCallbackService;
import pub.module.finance.api.service.BizPayNotifyService;
import pub.module.finance.curd.entity.FcAccountLog;

import jakarta.annotation.Resource;
@Service
public class BizPayCallbackServiceImpl implements BizPayCallbackService {
    @Resource
    BizFcAccountLogService bizFcAccountLogService;

    @Override
    public void callBizSystem(String tradeNo) {
        FcAccountLog result = bizFcAccountLogService.savePaidLog(tradeNo);
        Assert.notNull(result, "严重异常！，支付流水不存在！");
        result.setFcAcLogNotifyStatusCode(FcAcLogNotifyStatusCodeEnum.FAIL.getCode());
        if (StrUtil.isNotEmpty(result.getFcAcLogNotifyApi())) {
            if (result.getFcAcLogNotifyApi().startsWith("http")) {
                try (HttpResponse response = HttpUtil.createPost(result.getFcAcLogNotifyApi())
                        .contentType("application/json")
                        .body(JSONUtil.toJsonStr(result))
                        .execute()) {
                    String callBackResultSJson = response.body();
                    result.setFcAcLogNotifyResult(callBackResultSJson);
                    Assert.notEmpty(callBackResultSJson, "回调失败！");
                    Assert.isTrue(JSONUtil.isTypeJSON(callBackResultSJson), "回调失败,返回的消息不符合标准！");
                    result.setFcAcLogNotifyResult(callBackResultSJson);
                    if (200 == JSONUtil.parseObj(callBackResultSJson).getInt("code")) {
                        result.setFcAcLogNotifyStatusCode(FcAcLogNotifyStatusCodeEnum.OK.getCode());
                    } else {
                        result.setFcAcLogNotifyStatusCode(FcAcLogNotifyStatusCodeEnum.FAIL.getCode());
                    }
                }
            } else {
                BizPayNotifyService bizPayNotifyService = SpringUtil.getBean(result.getFcAcLogNotifyApi());
                try {
                    bizPayNotifyService.payCallBack(result);
                    result.setFcAcLogNotifyStatusCode(FcAcLogNotifyStatusCodeEnum.OK.getCode());
                } catch (Exception e) {
                    result.setFcAcLogNotifyResult(e.getMessage());
                    result.setFcAcLogNotifyStatusCode(FcAcLogNotifyStatusCodeEnum.FAIL.getCode());
                }
            }
        }
    }
}
