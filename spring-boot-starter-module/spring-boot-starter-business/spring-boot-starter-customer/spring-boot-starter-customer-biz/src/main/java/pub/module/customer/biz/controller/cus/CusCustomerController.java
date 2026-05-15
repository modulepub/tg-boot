package pub.module.customer.biz.controller.cus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CusCityResidenceOptionDTO;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.customer.curd.service.CustomerService;
import pub.module.system.api.util.UserUtil;


/**
 * 用户端-客户
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "用户端-客户")
@RestController
@RequestMapping("/cus/customer")
@Slf4j
public class CusCustomerController {

    @Resource
    CustomerService customerService;
    @Resource
    ApiCustomerService apiCustomerService;

    @Operation(summary = "用户端-客户-获取当前客户信息")
    @GetMapping(value = "/getCurrCusInfo")
    public Result<pub.module.customer.api.service.dto.CustomerDTO> getCurrCusInfo() {
        pub.module.customer.api.service.dto.CustomerDTO customer = apiCustomerService.getCusByUserCode(UserUtil.getCurrentSysUser().getUserCode());
        return Result.ok(customer);
    }

    @Operation(summary = "用户端-客户-城市列表（按常驻城市编码分组，含名称）")
    @GetMapping(value = "/getCitys")
    public Result<List<CusCityResidenceOptionDTO>> getCitys() {
        return Result.ok(customerService.listDistinctResidenceCities());
    }

    @Operation(summary = "用户端-客户-编辑客户信息（按客户编码绑定当前用户；请求体字段非空则更新）")
    @PostMapping(value = "/editCurrCusInfo")
    public Result<CustomerDTO> editCurrCusInfo(@RequestBody(required = false) Map<String, Object> body) {
        CustomerDTO dto = apiCustomerService.updateCurrCustomerPartial(
                UserUtil.getCurrentSysUser().getUserCode(),
                body == null ? Collections.emptyMap() : body);
        return Result.ok(dto);
    }
}