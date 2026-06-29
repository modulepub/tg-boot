package pub.module.wx.biz.controller.callback;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.wx.api.messaging.WxPayNotifyMessage;
import pub.module.wx.biz.messaging.WxPayNotifyPublisher;

/**
 * 微信支付回调（解析通知后通过 MQ 解耦下游业务）。
 */
@RestController
@Slf4j
@RequestMapping(value = "/pub/wechat")
public class WxPayCallbackController {

    @Resource
    private WxPayService wxPayService;
    @Resource
    private WxPayNotifyPublisher wxPayNotifyPublisher;

    @PostMapping(value = "/parseOrderNotifyResult")
    public String parseOrderNotifyResult(@RequestBody String jsonData, HttpServletRequest request) {
        try {
            log.info("parseOrderNotifyResult:{}", jsonData);
            SignatureHeader signatureHeader = buildSignatureHeader(request);
            WxPayOrderNotifyV3Result.DecryptNotifyResult notifyResult =
                    wxPayService.parseOrderNotifyV3Result(jsonData, signatureHeader).getResult();

            WxPayNotifyMessage message = new WxPayNotifyMessage();
            message.setOutTradeNo(notifyResult.getOutTradeNo());
            message.setTransactionId(notifyResult.getTransactionId());
            message.setTradeState(notifyResult.getTradeState());
            wxPayNotifyPublisher.publishAfterCommit(message);

            return WxPayNotifyResponse.success(notifyResult.getOutTradeNo());
        }
        catch (WxPayException e) {
            log.warn("微信回调验签或解析失败: {}", e.getMessage());
            return WxPayNotifyResponse.fail("微信回调有误!");
        }
    }

    private static SignatureHeader buildSignatureHeader(HttpServletRequest request) {
        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(request.getHeader("wechatpay-signature"));
        signatureHeader.setNonce(request.getHeader("wechatpay-nonce"));
        signatureHeader.setSerial(request.getHeader("wechatpay-serial"));
        signatureHeader.setTimeStamp(request.getHeader("wechatpay-timestamp"));
        return signatureHeader;
    }
}
