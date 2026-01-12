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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;
import pub.module.finance.api.constants.FcProductShelfStatusCodeEnum;
import pub.module.finance.api.service.BizFcProductService;
import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcProductService;

import jakarta.annotation.Resource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Tag(name = "信用借贷")
@RestController
@RequestMapping("/finance/biz/fcProduct")
@Slf4j
public class BizFcProductController {
    @Resource
    private IFcProductService fcProductService;
    @Resource
    private BizFcProductService bizFcProductService;

    @Operation(summary = "金融产品-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<FcProduct>> queryPageList(FcProduct fcProduct,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<FcProduct> queryWrapper = WebQueryUtil.buildQuery(fcProduct);
        queryWrapper.lambda().eq(FcProduct::getFcProductShelfStatusCode, FcProductShelfStatusCodeEnum.UP.getCode());
        Page<FcProduct> page = new Page<>(pageNo, pageSize);
        IPage<FcProduct> pageList = fcProductService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "借贷申请")
    public static class GetByProductCodeVO implements Serializable {
        /**
         * 产品编码
         */
        @Schema(description = "产品编码")
        private java.lang.String fcProductCode;
    }

    @Operation(summary = "金融产品-根据编码查询")
    @GetMapping(value = "/getByProductCode")
    public Result<FcProduct> getByProductCode(GetByProductCodeVO getByProductCodeVO) {
        FcProduct fcProduct = fcProductService.getOne(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductCode, getByProductCodeVO.getFcProductCode()), false);
        Assert.notNull(fcProduct, "系统预警：产品不存在");
        return Result.ok(fcProduct);
    }

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "借贷申请")
    public static class GetPeriodByProductCodeReqVO implements Serializable {
        /**
         * 产品编码
         */
        @Schema(description = "产品编码")
        private java.lang.String fcProductCode;
        @Schema(description = "申请金额")
        private java.math.BigDecimal fcLoanApyAmount;
    }

    @Operation(summary = "金融产品-分期计划")
    @GetMapping(value = "/getPeriodByProductCode")
    public Result<List<BizFcProductService.GetPeriodByProductCodeResDTO>> getPeriodByProductCode(GetPeriodByProductCodeReqVO getPeriodByProductCodeReqVO) {
        List<BizFcProductService.GetPeriodByProductCodeResDTO> result = bizFcProductService.getGetPeriodsByProductCode(getPeriodByProductCodeReqVO.getFcProductCode(), getPeriodByProductCodeReqVO.getFcLoanApyAmount());
        return Result.ok(result);
    }


    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "利息计算")
    public static class IcByProductReqVO implements Serializable {
        @Schema(description = "产品编码")
        private java.lang.String fcProductCode;
        @Schema(description = "分期期数")
        private int fcLoanPeriods;
        @Schema(description = "申请金额")
        private java.math.BigDecimal fcLoanApyAmount;
    }

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = false)
    @Schema(description = "利息计算")
    public static class IcByProductRespVO implements Serializable {
        @Schema(description = "产品编码")
        private java.lang.String fcProductCode;
        @Schema(description = "分期期数")
        private int fcLoanPeriods;
        @Schema(description = "申请金额")
        private java.math.BigDecimal fcLoanApyAmount;
        @Schema(description = "应还总金额")
        private java.math.BigDecimal fcLoanRepayableAmount;
        @Schema(description = "账单金额")
        private BigDecimal fcLoanBillAmount;
    }

    @Operation(summary = "利息试算-通过产品编码")
    @PostMapping(value = "/interestCalculationByFcProduct")
    public Result<?> interestCalculationByFcProduct(@RequestBody IcByProductReqVO icByProductReqVO) {
        FcProduct fcProduct = fcProductService.getOne(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductCode, icByProductReqVO.getFcProductCode()), false);
        BigDecimal borrowYears = new BigDecimal(icByProductReqVO.getFcLoanPeriods()).divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        BigDecimal yearInterestRate = fcProduct.getFcProductYearInterestRate();
        BigDecimal borrowAmount = icByProductReqVO.getFcLoanApyAmount();
        BigDecimal interestAmount = borrowAmount.multiply(yearInterestRate).multiply(borrowYears);
        IcByProductRespVO result = BeanUtil.copyProperties(icByProductReqVO, IcByProductRespVO.class);
        result.setFcLoanRepayableAmount(borrowAmount.add(interestAmount).setScale(2, RoundingMode.HALF_UP));
        result.setFcLoanBillAmount(result.getFcLoanRepayableAmount().divide(new BigDecimal(icByProductReqVO.getFcLoanPeriods()), 2, RoundingMode.HALF_UP));
        return Result.ok(result);
    }


}
