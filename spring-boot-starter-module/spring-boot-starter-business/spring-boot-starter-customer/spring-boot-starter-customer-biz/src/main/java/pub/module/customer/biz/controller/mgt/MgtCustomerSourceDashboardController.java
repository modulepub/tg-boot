package pub.module.customer.biz.controller.mgt;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.entity.CustomerContactRecord;
import pub.module.customer.curd.service.CustomerContactRecordService;
import pub.module.customer.curd.service.CustomerPromotionTaskService;
import pub.module.customer.curd.service.CustomerService;
import pub.module.customer.api.constants.CusSourceCodeEnum;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.common.model.vo.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 管理端-客户渠道
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@RestController
@RequestMapping("/mgt/customer/dashboard")
@Slf4j
public class MgtCustomerSourceDashboardController {

    @Resource
    ApiSysUserService apiSysUserService;
    @Resource
    CustomerPromotionTaskService customerPromotionTaskService;
    @Resource
    CustomerContactRecordService customerContactRecordService;
    @Resource
    CustomerService customerService;

    @Data
    public static class SourcePerformanceResVO {
        @Schema(description = "管理端-客户渠道来源")
        String cusSourceCode;
        @Schema(description = "管理端-客户总数")
        long cusTotal;
        @Schema(description = "管理端-联络总数")
        long customerContactTotal;
        @Schema(description = "管理端-无效客户数")
        BigDecimal InvalidCustomerTotal;
        @Schema(description = "管理端-接听数")
        long cusAnswerTotal;
        @Schema(description = "管理端-接听率")
        BigDecimal cusAnswerRate;
        @Schema(description = "管理端-有效通话客户数")
        long cusEffectiveCallDurationTotal;
        @Schema(description = "管理端-意向客户率")
        BigDecimal cusIntentionRate;
        @Schema(description = "管理端-成交率")
        BigDecimal cusSalesDealtRate;
    }

    @Data
    public static class SourcePerformanceReqVO {
        @Schema(description = "管理端-创建日期检索")
        private String[] createDateRangeArray;
    }

    @Operation(summary = "管理端-客户渠道分页列表查询")
    @GetMapping(value = "/resourceList")
    public Result<IPage<SourcePerformanceResVO>> resourceList(SourcePerformanceReqVO sourcePerformanceReqVO) {
        Assert.notNull(sourcePerformanceReqVO.getCreateDateRangeArray(),"统计日期不能为空！");
        QueryWrapper<Customer> customerQueryWrapper = new QueryWrapper<>();
        customerQueryWrapper.lambda().select(Customer::getCusSourceCode);
        customerQueryWrapper.lambda().groupBy(Customer::getCusSourceCode);
        List<String> sourceCodeList = customerService.list(customerQueryWrapper).stream()
                .map(Customer::getCusSourceCode)
                .filter(Objects::nonNull)
                .map(CusSourceCodeEnum::getCode)
                .distinct()
                .toList();
        List<SourcePerformanceResVO> records = new ArrayList<>();
        List<UserDTO> list = apiSysUserService.list(new UserDTO());
        for (String cusSourceCode : sourceCodeList) {
            SourcePerformanceResVO sourcePerformanceResVO = new SourcePerformanceResVO();
          sourcePerformanceResVO.setCusSourceCode(cusSourceCode);
            
            long cusTotal = customerService.count(
                    new QueryWrapper<Customer>()
                            .eq("cus_source_code", cusSourceCode)
                            .ge("DATE(create_time)", sourcePerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", sourcePerformanceReqVO.getCreateDateRangeArray()[1])
            );
            sourcePerformanceResVO.setCusTotal(cusTotal);
            String inSql = "select cus_code from customer where cus_source_code = '${cusSourceCode}'";
            inSql = inSql.replace("${cusSourceCode}", cusSourceCode);

            long customerContactTotal = customerContactRecordService.count(
                    new QueryWrapper<CustomerContactRecord>()
                            .inSql("user_code",inSql)
                            .ge("DATE(create_time)", sourcePerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", sourcePerformanceReqVO.getCreateDateRangeArray()[1])
            );
            sourcePerformanceResVO.setCustomerContactTotal(customerContactTotal);
            records.add(sourcePerformanceResVO);
        }
        IPage<SourcePerformanceResVO> page = new Page<>(1, records.size());
        page.setTotal(records.size());
        page.setRecords(records);
        return Result.ok(page);
    }


}