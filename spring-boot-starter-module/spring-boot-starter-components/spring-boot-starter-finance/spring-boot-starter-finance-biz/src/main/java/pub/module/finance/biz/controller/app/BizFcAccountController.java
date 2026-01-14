package pub.module.finance.biz.controller.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.finance.api.dto.FcAccountDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;
import pub.module.finance.api.constants.FcAcBindCardStatusCodeEnum;
import pub.module.finance.api.constants.FcProductTypeCodeEnum;
import pub.module.finance.api.service.BizFcAccountService;
import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcAccountService;
import pub.module.finance.curd.service.IFcProductService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;


/**
 * 金融账户
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-09
 */
@Tag(name = "综合支付")
@RestController
@RequestMapping("/finance/biz/fcAccount")
@Slf4j
public class BizFcAccountController {
    @Resource
    private IFcAccountService fcAccountService;
    @Resource
    private BizFcAccountService bizBankAccountService;
    @Resource
    BizFcAccountService bizFcAccountService;
    @Resource
    IFcProductService fcProductService;


    @Operation(summary = "查询各类金融账户-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FcAccount>> queryPageList(FcAccount fcAccount,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<FcAccount> queryWrapper = WebQueryUtil.buildQuery(fcAccount);
        if (StrUtil.isEmpty(fcAccount.getFcProductCode()) && StrUtil.isEmpty(fcAccount.getFcProductTypeCode())) {
            queryWrapper.lambda().eq(FcAccount::getFcProductTypeCode, FcProductTypeCodeEnum.BANK.getCode());
            queryWrapper.lambda().eq(FcAccount::getFcAcBindCardStatusCode, FcAcBindCardStatusCodeEnum.YES.getCode());
        }
        queryWrapper.lambda().eq(FcAccount::getFcAcSysUserCode, UserUtil.getCurrentSysUser().getUserCode());
        Page<FcAccount> page = new Page<>(pageNo, pageSize);
        IPage<FcAccount> pageList = fcAccountService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Data
    @Schema(description = "根据编码查询账户详情")
    public static class QueryByCodeVO {
        @Schema(description = "金融账户编码")
        String fcAcCode;
    }

    @Operation(summary = "根据编码查询账户详情")
    @GetMapping(value = "/queryFcAccountByFcAcCode")
    public Result<FcAccount> queryFcAccountByFcAcCode(QueryByCodeVO queryByCodeVO) {
        QueryWrapper<FcAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FcAccount::getFcAcCode, queryByCodeVO.getFcAcCode());
        FcAccount result = fcAccountService.getOne(queryWrapper, false);
        return Result.ok(result);
    }

    @Data
    @Schema(description = "通过产品编码获取用户额度")
    public static class GetFcAccountByProductCodeVO {
        @Schema(title = "产品编码", description = "产品编码")
        String fcProductCode;
    }

    @Operation(summary = "通过产品编码获取用户额度", description = "额度来源于针对于该产品的授信")
    @GetMapping(value = "/getFcAccountByProductCode")
    public Result<FcAccountDTO> getCreditLimitByProductCode( GetFcAccountByProductCodeVO getFcAccountByProductCodeVO) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        long count = fcProductService.count(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductCode, getFcAccountByProductCodeVO.getFcProductCode()));
        Assert.isTrue(count > 0, "严重告警：产品不存在");
        FcAccountDTO result = bizFcAccountService.getAccount(userCode, getFcAccountByProductCodeVO.getFcProductCode());
        return Result.ok(result);
    }

    @Data
    @Schema(description = "绑定银行卡VO")
    public static class BindBankCardSureVO {
        @Schema(description = "账户编码（四要素绑卡确认时候用）")
        private java.lang.String fcAcCode;
        @Schema(description = "验证码1")
        private java.lang.String fcBankCardAuthCode1;
        @Schema(description = "验证码2")
        private java.lang.String fcBankCardAuthCode2;
        @Schema(description = "VIP开通渠道")
        private java.lang.String vipOpenChannel;
        @Schema(description = "VIP开通选中")
        private java.lang.Integer vipOpenCheck;
    }

    @Operation(summary = "四要素绑卡确认")
    @PostMapping(value = "/bindCard")
    public Result<FcAccountDTO> bindCard(@RequestBody BindBankCardSureVO bindBankCardSureVO) {
        BizFcAccountService.BindBankCardSureDTO bindBankCardSureDTO = BeanUtil.copyProperties(bindBankCardSureVO, BizFcAccountService.BindBankCardSureDTO.class);
        UserDTO sysUser = UserUtil.getCurrentSysUser();
        bindBankCardSureDTO.setFcAcSysUserCode(sysUser.getUserCode());
        FcAccountDTO fcAccount = bizBankAccountService.bindBankCardSure(bindBankCardSureDTO);


        return Result.ok("綁卡成功！", fcAccount);
    }

    @Operation(summary = "绑卡短信")
    @PostMapping(value = "/bindCardSms")
    public Result<FcAccountDTO> bindCardSms(@RequestBody BizFcAccountService.BindCardSmsDTO bindCardSmsDTO) {
        UserDTO sysUser = UserUtil.getCurrentSysUser();
        bindCardSmsDTO.setFcAcSysUserCode(sysUser.getUserCode());
        FcAccountDTO fcAccount = bizBankAccountService.bindBankCardSms(bindCardSmsDTO);
        return Result.ok("绑卡短信发送成功", fcAccount);
    }


    @Data
    @Schema(description = "更改默认")
    public static class FcAccountVO {
        @Schema(description = "编码")
        private java.lang.String fcAcCode;
    }

    @Operation(summary = "更改默认")
    @PostMapping(value = "/changeDefault")
    public Result<String> changeDefault(@RequestBody FcAccountVO changeDefaultVO) {
        fcAccountService.update(new UpdateWrapper<FcAccount>().lambda().set(FcAccount::getFcAcDefaultStatusCode, 1).eq(FcAccount::getFcAcCode, changeDefaultVO.getFcAcCode()));
        fcAccountService.update(new UpdateWrapper<FcAccount>().lambda().set(FcAccount::getFcAcDefaultStatusCode, 0).ne(FcAccount::getFcAcCode, changeDefaultVO.getFcAcCode()));
        return Result.ok("更改默认成功！");
    }

    @Operation(summary = "解绑")
    @PostMapping(value = "/cancelBindCard")
    public Result<String> cancelBindCard(@RequestBody FcAccountVO fcAccountVO) {
        fcAccountService.remove(new QueryWrapper<FcAccount>().lambda().eq(FcAccount::getFcAcCode, fcAccountVO.getFcAcCode()));
        return Result.ok("解绑成功！");
    }

}
