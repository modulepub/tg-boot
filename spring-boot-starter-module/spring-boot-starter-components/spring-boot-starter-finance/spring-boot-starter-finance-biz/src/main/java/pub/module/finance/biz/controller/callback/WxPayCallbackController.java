package pub.module.finance.biz.controller.callback;

import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.finance.api.service.BizPayCallbackService;

import jakarta.annotation.Resource;


@RestController
@Slf4j
@RequestMapping(value = "/pub/wechat")
public class WxPayCallbackController {

    @Resource
    WxPayService wxPayService;
    @Resource
    BizPayCallbackService payCallbackService;
    /**
     * 微信支付回调
     *
     * @return String
     */
    @PostMapping(value = "/parseOrderNotifyResult")
    public String parseOrderNotifyResult(@RequestBody String jsonData, HttpServletRequest request) {
        try {
            log.info("parseOrderNotifyResult:{}", jsonData);
            String signature = request.getHeader("wechatpay-signature");
            String nonce = request.getHeader("wechatpay-nonce");
            String serial = request.getHeader("wechatpay-serial");
            String timestamp = request.getHeader("wechatpay-timestamp");
            SignatureHeader signatureHeader = new SignatureHeader();
            signatureHeader.setSignature(signature);
            signatureHeader.setNonce(nonce);
            signatureHeader.setSerial(serial);
            signatureHeader.setTimeStamp(timestamp);
            payCallbackService.callBizSystem(wxPayService.parseOrderNotifyV3Result(jsonData, signatureHeader).getResult().getOutTradeNo());
            return WxPayNotifyResponse.success("微信回调成功！");
        } catch (WxPayException e) {
            return WxPayNotifyResponse.fail("微信回调有误!");
        }
    }


}
