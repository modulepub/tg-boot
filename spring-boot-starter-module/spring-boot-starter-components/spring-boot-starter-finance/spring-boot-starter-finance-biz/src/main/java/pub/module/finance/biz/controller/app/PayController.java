package pub.module.finance.biz.controller.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.finance.api.dto.FcAccountDTO;
import pub.module.finance.api.service.BizFcAccountLogService;
import pub.module.finance.api.service.BizFcAccountService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.vo.Result;
import pub.module.finance.api.service.BizPayService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;
import java.math.BigDecimal;

@RestController
@RequestMapping("/pay")
@Tag(name = "综合支付")
@Slf4j
public class PayController {

    @Resource
    BizFcAccountService fcAccountService;
    @Resource
    BizFcAccountLogService fcAccountLogService;

    @Data
    @Schema(description = "预支付VO")

    public static class PrePayVO {
        @Schema(description = "业务交易号（多个以逗号分隔，字符串长度不超过1000位）")
        public String tradeNo;
        @Schema(description = "金额（业务系统需要校验金额，支付模块不对金额进行校验）")
        BigDecimal amount;
        @Schema(description = "备注")
        String remark;
        @Schema(description = "账户编码（当使用微信支付宝直接支付时可留空，微信、支付宝自动扣款功能需要传入）")
        private java.lang.String fcAcCode;
        @Schema(description = "支付平台参数：前端根据各类支付平台传参，参数名称与平台提供的一致")
        private JSONObject platParam;
        @Schema(description = "回调服务地址：支持http地址和api名称2种方式，回调参数为支付流水实体")
        public String notifyApi;
        @Schema(description = "支付密码")
        String password;

    }

    @Operation(summary = "预支付接口", description = "支持信用支付、余额支付、支付宝立即支付、支付宝签约自动扣款/免密支付、微信立即支付、银行卡签约代扣等")
    @PostMapping("/prePay")
    public Result<BizPayService.PrePayDTO.Res> prePay(@RequestBody PrePayVO prePayVO) {
        UserDTO loginUser = UserUtil.getCurrentSysUser();
        BizPayService.PrePayDTO.Req prePayDTO = BeanUtil.copyProperties(prePayVO, BizPayService.PrePayDTO.Req.class);
        prePayDTO.setUserCode(loginUser.getUserCode());
        prePayDTO.setUserRealName(loginUser.getUserRealName());
        log.info("支付参数：prePayVO:{}", prePayVO);
        FcAccountDTO fcAccount = fcAccountService.getAccount(prePayDTO.getFcAcCode());
        Assert.notNull(fcAccount, "严重异常，查询不存在的账户！");
        BizPayService bizPayService = SpringUtil.getBean(fcAccount.getFcProductTypeCode(), BizPayService.class);
        return Result.ok(bizPayService.prePay(prePayDTO));
    }


    @Operation(summary = "获取支付结果", description = "一定要调用，业务端支付状态回调从本接口出发，调借此回调几次，注意状态处理！")
    @PostMapping("/getPayResult")
    public Result<BizPayService.QueryPayResultDTO.Res> getPayResult(@RequestBody BizPayService.QueryPayResultDTO.Req req) {
        BizPayService bizPayService = SpringUtil.getBean(fcAccountLogService.getFcProductCodeBy(req.getTradeNo()), BizPayService.class);
        return Result.ok(bizPayService.getPayResult(req));
    }


}
