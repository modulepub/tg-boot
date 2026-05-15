package pub.module.customer.biz.controller.mgt;

import java.util.Collection;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.customer.curd.entity.Customer;
import pub.module.customer.curd.service.CustomerService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * 管理端-客户
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "管理端-客户")
@RestController
@RequestMapping("/mgt/customer/customer")
@Slf4j
public class MgtCustomerController {

    @Resource
    CustomerService customerService;
    @Resource
    ApiCustomerService apiCustomerService;

    @Operation(summary = "管理端-客户分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Customer>> queryPageList(Customer customer, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<Customer> queryWrapper = WebQueryUtil.buildQuery(customer);
        if (customer.getCusAssignSalesTimeRangeArray() != null) {
            queryWrapper.ge("DATE(cus_assign_sales_time)", customer.getCusAssignSalesTimeRangeArray()[0]);
            queryWrapper.le("DATE(cus_assign_sales_time)", customer.getCusAssignSalesTimeRangeArray()[1]);
        }
        Page<Customer> page = new Page<>(pageNo, pageSize);
        IPage<Customer> pageList = customerService.page(page, queryWrapper);
        return Result.ok(pageList);
    }
    @Operation(summary = "管理端-客户推送成功")
    @PostMapping(value = "/push")
    public Result<String> push(@RequestBody Map<String,Object> customer) {
        apiCustomerService.importData(customer);
        return Result.ok("推送成功！");
    }

    @Operation(summary = "管理端-客户添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Customer customer) {
        customerService.save(customer);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-客户编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody Customer customer) {
        customerService.updateById(customer);
        return Result.ok("编辑成功!");
    }


    @Operation(summary = "管理端-客户批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.customerService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-客户入库")
    @PostMapping(value = "/inPool")
    public Result<String> inPool(@RequestBody Collection<String> list) {
        for (String id : list) {
            UpdateWrapper<Customer> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(Customer::getId, id);
            updateWrapper.lambda().set(Customer::getCusPoolStatusCode, "1");
            this.customerService.update(updateWrapper);
        }
        return Result.ok("批量入库成功!");
    }

    @Operation(summary = "管理端-客户通过id查询")
    @GetMapping(value = "/queryById")
    public Result<Customer> queryById(@RequestParam(name = "id") String id) {
        Customer customer = customerService.getById(id);
        return Result.ok(customer);
    }

}