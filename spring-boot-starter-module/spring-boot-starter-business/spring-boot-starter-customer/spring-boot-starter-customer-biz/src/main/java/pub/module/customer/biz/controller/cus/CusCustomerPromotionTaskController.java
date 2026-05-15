package pub.module.customer.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.customer.biz.service.SpiCustomerPromotionRelationService;
import pub.module.customer.curd.entity.CustomerPromotionTask;
import pub.module.customer.curd.service.CustomerPromotionTaskService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;


/**
 * 用户端-客户营销关系
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "用户端-客户端-营销池客户")
@RestController
@RequestMapping("/cus/customer/customerPromotionTask")
@Slf4j
public class CusCustomerPromotionTaskController {
    @Resource
    private CustomerPromotionTaskService customerPromotionTaskService;
    @Resource
    private SpiCustomerPromotionRelationService spiCustomerPromotionRelationService;


    @Operation(summary = "用户端-客户端-营销池客户分页列表查询")
    @GetMapping(value = "/myCusList")
    public Result<IPage<CustomerPromotionTask>> notDealList(CustomerPromotionTask customerPromotionTask, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<CustomerPromotionTask> queryWrapper = WebQueryUtil.buildQuery(customerPromotionTask);
        if(customerPromotionTask.getCreateDateRangeArray()!=null&& customerPromotionTask.getCreateDateRangeArray().length==2){
            queryWrapper.ge("DATE(create_time)", customerPromotionTask.getCreateDateRangeArray()[0]);
            queryWrapper.le("DATE(create_time)", customerPromotionTask.getCreateDateRangeArray()[1]);
        }
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        queryWrapper.lambda().eq(CustomerPromotionTask::getUserCode, userDTO.getUserCode());
        Page<CustomerPromotionTask> page = new Page<>(pageNo, pageSize);
        IPage<CustomerPromotionTask> pageList = customerPromotionTaskService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Data
    public static class DealtVO {
        @Schema(description = "用户端-编码")
        private String promotionTaskCode;
    }


    @Operation(summary = "用户端-客户端-成交")
    @PostMapping(value = "/dealt")
    public Result<?> dealt(@RequestBody DealtVO dealtVO) {
        spiCustomerPromotionRelationService.dealt(dealtVO.getPromotionTaskCode());
        return Result.ok("success");
    }

    @Operation(summary = "用户端-客户端-完单")
    @PostMapping(value = "/complete")
    public Result<?> complete(@RequestBody DealtVO dealtVO) {
        spiCustomerPromotionRelationService.complete(dealtVO.getPromotionTaskCode());
        return Result.ok("success");
    }

    @Operation(summary = "用户端-客户端-营销池客户通过id查询")
    @GetMapping(value = "/queryById")
    public Result<CustomerPromotionTask> queryById(@RequestParam(name = "id") String id) {
        CustomerPromotionTask customerPromotionTask = customerPromotionTaskService.getById(id);
        return Result.ok(customerPromotionTask);
    }

}