package pub.module.finance.biz.controller.app;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;
import pub.module.finance.api.service.BizFcCreditExtensionService;
import pub.module.finance.api.service.BizFcLoanService;
import pub.module.finance.curd.entity.FcLoan;
import pub.module.finance.curd.service.IFcLoanService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;

import java.io.Serializable;

/**
 * 信用借贷
 *
 * @author tg
 * @version V1.0
 * @since 2025-10-02
 */

@Tag(name = "信用借贷")
@RestController
@RequestMapping("/finance/biz/fcLoan")
@Slf4j
public class BizFcLoanController {
    @Resource
    private IFcLoanService fcLoanService;
    @Resource
    private BizFcCreditExtensionService bizFcCreditExtensionService;
    @Resource
    private BizFcLoanService bIzFcLoanService;

    @Operation(summary = "信用借贷-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FcLoan>> queryPageList(FcLoan fcLoan,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<FcLoan> queryWrapper = WebQueryUtil.buildQuery(fcLoan);
        queryWrapper.lambda().eq(FcLoan::getUserCode, UserUtil.getCurrentSysUser().getUserCode());
        Page<FcLoan> page = new Page<>(pageNo, pageSize);
        IPage<FcLoan> pageList = fcLoanService.page(page, queryWrapper);
        return Result.ok(pageList);
    }


    @Data
    @Schema(description = "根据借贷编码查询详情")
    public static class GetFcLoanByCodeVO {
        @Schema(description = "借贷编码")
        String fcLoanCode;
    }

    @Operation(summary = "信用借贷-根据借贷编码查询详情")
    @GetMapping(value = "/getFcLoanByCode")
    public Result<FcLoan> getFcLoanByCode(GetFcLoanByCodeVO getFcLoanByCodeVO) {
        QueryWrapper<FcLoan> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FcLoan::getFcLoanCode, getFcLoanByCodeVO.getFcLoanCode());
        FcLoan result = fcLoanService.getOne(queryWrapper, false);
        return Result.ok(result);
    }

    @Data
    @Schema(description = "借贷申请")
    public static class FcLoanVO implements Serializable {
        @Schema(description = "产品编码")
        private java.lang.String fcProductCode;
        @Schema(description = "商品编码")
        private java.lang.String mlGoodsCode;
        @Schema(description = "订单编码")
        private java.lang.String mlOrderCode;
        @Schema(description = "申请金额")
        private java.math.BigDecimal fcLoanApyAmount;
        @Schema(description = "分期期数")
        private java.lang.Integer fcLoanPeriods;
        @Schema(description = "收款账户编码")
        private java.lang.String fcAcCode;
        @Schema(description = "借款用途")
        private java.lang.String fcLoanUseTypeCode;
        @Schema(description = "VIP开通渠道")
        private java.lang.String vipOpenChannel;
        @Schema(description = "VIP开通选中")
        private java.lang.Integer vipOpenCheck;
    }

    @Operation(summary = "信用借贷-借贷申请")
    @PostMapping(value = "/apply")
    public Result<String> apply(@RequestBody FcLoanVO fcLoanVO) {
        BizFcLoanService.FcLoanDTO fcLoanDTO = BeanUtil.copyProperties(fcLoanVO, BizFcLoanService.FcLoanDTO.class);
        Assert.notNull(fcLoanDTO.getFcLoanPeriods(), "分期期数必传");
        UserDTO sysUser = UserUtil.getCurrentSysUser();
        fcLoanDTO.setUserCode(sysUser.getUserCode());
        bIzFcLoanService.apply(fcLoanDTO);

        return Result.ok("申请成功！");
    }

    @Data
    @Schema(description = "授信申请")
    public static class CreditApplyVO implements Serializable {
        @Schema(description = "产品编码，多个以逗号分隔")
        private java.lang.String fcProductCode;
        @Schema(description = "VIP开通渠道")
        private java.lang.String vipOpenChannel;
        @Schema(description = "VIP开通选中")
        private java.lang.Integer vipOpenCheck;
    }

    @Operation(summary = "信用借贷-授信申请")
    @PostMapping(value = "/creditApply")
    public Result<?> creditApply(@RequestBody CreditApplyVO creditApplyVO) {
        BizFcCreditExtensionService.CreditApplyDTO creditApplyDTO = BeanUtil.copyProperties(creditApplyVO, BizFcCreditExtensionService.CreditApplyDTO.class);
        UserDTO sysUser = UserUtil.getCurrentSysUser();
        creditApplyDTO.setUserCode(sysUser.getUserCode());
        bizFcCreditExtensionService.credit(creditApplyDTO);
        return Result.ok("授信发起成功");
    }


}
