package pub.module.customer.biz.controller.pub;

import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.service.CustomerService;
import pub.module.common.model.vo.Result;


/**
 * 公开-客户
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "公开-客户")
@RestController
@RequestMapping("/pub/customer/customer")
@Slf4j
public class PubCustomerController {

    @Resource
    CustomerService customerService;

    @Operation(summary = "公开-客户通过客户编码查询")
    @GetMapping(value = "/queryByCusCode")
    public Result<Customer> queryById(@RequestParam(name = "cusCode") String cusCode) {
        Customer customer = customerService.getByCode(cusCode);
        Assert.notNull(customer,"严重异常，未查询到客户！");
        return Result.ok(customer);
    }

}