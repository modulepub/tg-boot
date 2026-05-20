package pub.module.wx.biz.controller;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import pub.module.common.model.vo.Result;
import pub.module.wx.biz.utils.WxUtil;
import pub.module.wx.biz.vo.LoginRequest;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 公开-微信控制器
 * 提供微信公众号和小程序相关的API接口
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@RestController
@Slf4j
@RequestMapping("/pub/wx")
public class WxController {
    @Resource
    WxMpService wxMpService;
    @Resource
    WxMpMessageRouter messageRouter;


    @PostMapping("/getWxMaUserInfo")
    public Result<WxMaUserInfo> getWxMaUserInfo(@RequestBody LoginRequest request) {
        return Result.ok(WxUtil.getWxMaUserInfo(request));
    }

    @PostMapping("/getWxMpUserInfo")
    public Result<WxMpUser> getWxMpUserInfo(@RequestBody LoginRequest request) {
        return Result.ok(WxUtil.getWxMpUserInfo(request));
    }

    @Data
    public static class WxaCodeUnlimitRequest {
        private String appId;
        /** 页面路径，勿带前导 `/` */
        private String page;
        /** 场景值，最长 32 字符（扫码后在 onLoad 的 scene 字段） */
        private String scene;
        /** develop | trial | release */
        private String envVersion;
    }

    @Data
    public static class PosterImageRequest {
        private String url;
    }

    @Operation(summary = "公开-拉取海报用远程图片（转 data URL）")
    @PostMapping("/fetchPosterImageData")
    public Result<String> fetchPosterImageData(@RequestBody PosterImageRequest request) {
        try {
            return Result.ok(WxUtil.fetchPosterImageDataUrl(request.getUrl()));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.warn("拉取海报图片失败: {}", e.getMessage());
            return Result.error("图片加载失败");
        }
    }

    @Operation(summary = "公开-生成小程序码（无数量限制）")
    @PostMapping("/getWxaCodeUnlimit")
    public Result<String> getWxaCodeUnlimit(@RequestBody WxaCodeUnlimitRequest request) {
        try {
            byte[] png = WxUtil.getWxaCodeUnlimit(
                    request.getAppId(),
                    request.getPage(),
                    request.getScene(),
                    request.getEnvVersion()
            );
            return Result.ok(Base64.encode(png));
        } catch (WxErrorException e) {
            log.warn("生成小程序码失败: {}", e.getMessage());
            return Result.error(e.getError().getErrorMsg());
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 仅支持明文模式
     */
    @RequestMapping(value = "/mpConfig/{appid}", produces = "application/xml; charset=UTF-8")
    public String post(
            @PathVariable("appid") String appid,
            @RequestBody(required = false) String requestBody,
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "openid", required = false) String openid,
            @RequestParam(name = "echostr", required = false) String echostr,
            @RequestParam(name = "encrypt_type", required = false) String encType,
            @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info("收到来自微信服务器的请求：[{},{},{},{},{},{},{},{},{}]", appid, requestBody, signature, timestamp, nonce, openid, echostr, encType, msgSignature);
        wxMpService.switchover(appid);
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = null;
        if (encType == null && StrUtil.isNotEmpty(requestBody)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.messageRouter.route(inMessage);
            if (outMessage != null) {
                out = outMessage.toXml();
            }
        } else {
            out = echostr;
        }
        return out;
    }


}
