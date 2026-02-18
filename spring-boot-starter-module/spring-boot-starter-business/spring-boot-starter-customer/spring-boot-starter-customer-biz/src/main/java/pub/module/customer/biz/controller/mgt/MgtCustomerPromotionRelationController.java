package pub.module.customer.biz.controller.mgt;

import java.util.Collection;
import java.util.List;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import pub.module.customer.api.constants.CusDealtStatusCodeEnum;
import pub.module.customer.biz.service.SpiCustomerPromotionRelationService;
import pub.module.customer.curd.entity.CustomerPromotionRelation;
import pub.module.customer.curd.service.CustomerPromotionRelationService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * 客户营销关系 Controller
 *
 * @author tg
 * 2026-02-03 00:58:36
 */
@Tag(name = "客户营销关系 CURD 处理器")
@RestController
@RequestMapping("/mgt/customer/customerPromotionRelation")
@Slf4j
public class MgtCustomerPromotionRelationController {
    @Resource
    private CustomerPromotionRelationService customerPromotionRelationService;
    @Resource
    SpiCustomerPromotionRelationService spiCustomerPromotionRelationService;


    @Operation(summary = "服务池管理 - 分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<CustomerPromotionRelation>> list(CustomerPromotionRelation customerPromotionRelation, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<CustomerPromotionRelation> queryWrapper = WebQueryUtil.buildQuery(customerPromotionRelation);
        if (customerPromotionRelation.getCreateDateRangeArray() != null) {
            queryWrapper.ge("DATE(create_time)", customerPromotionRelation.getCreateDateRangeArray()[0]);
            queryWrapper.le("DATE(create_time)", customerPromotionRelation.getCreateDateRangeArray()[1]);
        }
        Page<CustomerPromotionRelation> page = new Page<>(pageNo, pageSize);
        IPage<CustomerPromotionRelation> pageList = customerPromotionRelationService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Data
    public static class AssignTelemarketerVO {
        List<String> cusCodeList;
        List<String> userCodeList;
        String promotionTaskCode;
    }

    @Operation(summary = "客户 - 分配")
    @PostMapping(value = "/assign")
    public Result<String> assignTelemarketer(@RequestBody AssignTelemarketerVO assignTelemarketerVO) {
        Assert.notBlank(assignTelemarketerVO.getPromotionTaskCode(), "任务编码不能为空");
        spiCustomerPromotionRelationService.assign(assignTelemarketerVO.getPromotionTaskCode(), assignTelemarketerVO.getCusCodeList(), assignTelemarketerVO.getUserCodeList());
        return Result.ok("批量分配成功!");
    }

    @Operation(summary = "客户营销关系 - 添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody CustomerPromotionRelation customerPromotionRelation) {

        customerPromotionRelationService.save(customerPromotionRelation);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "客户营销关系 - 编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody CustomerPromotionRelation customerPromotionRelation) {
        customerPromotionRelationService.updateById(customerPromotionRelation);
        return Result.ok("编辑成功!");
    }


    @Operation(summary = "客户营销关系 - 批量删除")
    @PostMapping(value = "/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        this.customerPromotionRelationService.removeByIds(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "客户营销关系 - 通过id查询")
    @GetMapping(value = "/queryById")
    public Result<CustomerPromotionRelation> queryById(@RequestParam(name = "id") String id) {
        CustomerPromotionRelation customerPromotionRelation = customerPromotionRelationService.getById(id);
        return Result.ok(customerPromotionRelation);
    }

}