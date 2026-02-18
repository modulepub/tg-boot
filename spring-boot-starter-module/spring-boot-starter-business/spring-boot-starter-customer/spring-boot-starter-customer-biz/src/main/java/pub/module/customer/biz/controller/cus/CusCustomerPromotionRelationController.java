package pub.module.customer.biz.controller.cus;

import cn.hutool.core.util.StrUtil;
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
import pub.module.customer.curd.entity.CustomerPromotionRelation;
import pub.module.customer.curd.service.CustomerPromotionRelationService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;


/**
 * 客户营销关系 Controller
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "客户端-营销池客户")
@RestController
@RequestMapping("/cus/customer/customerPromotionRelation")
@Slf4j
public class CusCustomerPromotionRelationController {
    @Resource
    private CustomerPromotionRelationService customerPromotionRelationService;
    @Resource
    private SpiCustomerPromotionRelationService spiCustomerPromotionRelationService;


    @Operation(summary = "客户端-营销池客户 - 分页列表查询")
    @GetMapping(value = "/myCusList")
    public Result<IPage<CustomerPromotionRelation>> notDealList(CustomerPromotionRelation customerPromotionRelation, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<CustomerPromotionRelation> queryWrapper = WebQueryUtil.buildQuery(customerPromotionRelation);
        if(customerPromotionRelation.getCreateDateRangeArray()!=null){
            queryWrapper.ge("DATE(create_time)", customerPromotionRelation.getCreateDateRangeArray()[0]);
            queryWrapper.le("DATE(create_time)", customerPromotionRelation.getCreateDateRangeArray()[1]);
        }
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        queryWrapper.lambda().eq(CustomerPromotionRelation::getUserCode, userDTO.getUserCode());
        Page<CustomerPromotionRelation> page = new Page<>(pageNo, pageSize);
        IPage<CustomerPromotionRelation> pageList = customerPromotionRelationService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Data
    public static class DealtVO {
        @Schema(description = "编码")
        private String promotionRelCode;
    }


    @Operation(summary = "客户端-成交")
    @PostMapping(value = "/dealt")
    public Result<?> dealt(@RequestBody DealtVO dealtVO) {
        spiCustomerPromotionRelationService.dealt(dealtVO.getPromotionRelCode());
        return Result.ok("success");
    }

    @Operation(summary = "客户端-完单")
    @PostMapping(value = "/complete")
    public Result<?> complete(@RequestBody DealtVO dealtVO) {
        spiCustomerPromotionRelationService.complete(dealtVO.getPromotionRelCode());
        return Result.ok("success");
    }

    @Operation(summary = "客户端-营销池客户 - 通过id查询")
    @GetMapping(value = "/queryById")
    public Result<CustomerPromotionRelation> queryById(@RequestParam(name = "id") String id) {
        CustomerPromotionRelation customerPromotionRelation = customerPromotionRelationService.getById(id);
        return Result.ok(customerPromotionRelation);
    }

}