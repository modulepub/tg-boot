package pub.module.finance.biz.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import pub.module.finance.api.service.BizFcProductService;
import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcProductService;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class BizFcProductServiceImpl implements BizFcProductService {
    @Resource
    private IFcProductService fcProductService;
    @Override
    public  List<GetPeriodByProductCodeResDTO> getGetPeriodsByProductCode(String fcProductCode,BigDecimal fcLoanApyAmount) {
        FcProduct fcProduct = fcProductService.getOne(new QueryWrapper<FcProduct>().lambda().eq(FcProduct::getFcProductCode, fcProductCode),false);
        if(fcLoanApyAmount==null){
            fcLoanApyAmount = fcProduct.getFcProductMaxAmount();
        }
        if(fcLoanApyAmount==null){
            fcLoanApyAmount = new BigDecimal(1000);
        }
        Assert.notNull(fcProduct, "系统预警：产品不存在");
        int periodsSub = fcProduct.getFcProductMaxPeriod()/ fcProduct.getFcProductMinPeriod();
        List<GetPeriodByProductCodeResDTO> result = new ArrayList<>();
        for (int i = 1; i <= periodsSub; i++) {
            GetPeriodByProductCodeResDTO getPeriodByProductCodeResDTO = getGetPeriodByProductCodeResDTO(fcLoanApyAmount, i, fcProduct);
            result.add(getPeriodByProductCodeResDTO);
        }
        return result;
    }

    private static GetPeriodByProductCodeResDTO getGetPeriodByProductCodeResDTO(BigDecimal fcLoanApyAmount, int i, FcProduct fcProduct) {
        BigDecimal period = new BigDecimal(i * fcProduct.getFcProductMinPeriod());
        BigDecimal years = period.divide(new BigDecimal(12),2,RoundingMode.HALF_UP);
        BigDecimal yearInterest = fcLoanApyAmount.multiply(years).multiply(fcProduct.getFcProductYearInterestRate());
        BigDecimal totalInterest = yearInterest.multiply(years);
        return getGetPeriodByProductCodeResDTO(fcLoanApyAmount, totalInterest, period);
    }

    private static GetPeriodByProductCodeResDTO getGetPeriodByProductCodeResDTO(BigDecimal fcLoanApyAmount, BigDecimal totalInterest, BigDecimal period) {
        BigDecimal totalRepaymentAmount = fcLoanApyAmount.add(totalInterest);
        GetPeriodByProductCodeResDTO getPeriodByProductCodeResDTO = new GetPeriodByProductCodeResDTO();
        getPeriodByProductCodeResDTO.setName(period + "期");
        getPeriodByProductCodeResDTO.setPeriods(period.longValue());
        getPeriodByProductCodeResDTO.setRepayAmount(totalRepaymentAmount.divide(period,2,RoundingMode.HALF_UP));
        getPeriodByProductCodeResDTO.setInterestAmount(totalInterest.divide(period,2,RoundingMode.HALF_UP));
        getPeriodByProductCodeResDTO.setDiscount(new BigDecimal("8"));
        return getPeriodByProductCodeResDTO;
    }


}
