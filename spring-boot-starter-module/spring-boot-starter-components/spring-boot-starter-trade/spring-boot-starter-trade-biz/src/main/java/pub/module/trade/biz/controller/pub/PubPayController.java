package pub.module.trade.biz.controller.pub;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.trade.biz.service.BizPayService;
import pub.module.trade.biz.service.BizPayServiceRegistry;
import pub.module.trade.crud.entity.TdOrder;

@RestController
@RequestMapping("/pub/pay")
@Tag(name = "公开-综合支付")
@Slf4j
public class PubPayController {

    @Resource
    private BizPayServiceRegistry bizPayServiceRegistry;
    @Operation(summary = "公开-综合支付-预支付接口", description = "支持信用支付、余额支付、支付宝立即支付、支付宝签约自动扣款/免密支付、微信立即支付、银行卡签约代扣等")
    @PostMapping("/prePay")
    public Result<BizPayService.PrePayDTO.Res> prePay(@RequestBody BizPayService.PrePayDTO.Req prePayVO) {
        BizPayService.PrePayDTO.Req prePayDTO = BeanUtil.copyProperties(prePayVO, BizPayService.PrePayDTO.Req.class);
        log.info("支付参数：prePayVO:{}", prePayVO);
        BizPayService bizPayService = bizPayServiceRegistry.require(prePayVO.getTdPaidChannelCode());
        return Result.ok(bizPayService.prePay(prePayDTO));
    }


    @Operation(summary = "公开-综合支付-获取支付结果", description = "一定要调用，业务端支付状态回调从本接口出发，调借此回调几次，注意状态处理！")
    @PostMapping("/getPayResult")
    public Result<TdOrder> getPayResult(@RequestBody BizPayService.PayResultReq req) {
        BizPayService bizPayService = bizPayServiceRegistry.require(req.getTdPaidChannelCode());
        return Result.ok(bizPayService.getPayResult(req));
    }


}
