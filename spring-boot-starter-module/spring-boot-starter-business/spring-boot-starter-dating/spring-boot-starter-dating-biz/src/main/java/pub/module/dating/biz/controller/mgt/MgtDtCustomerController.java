package pub.module.dating.biz.controller.mgt;

import pub.module.common.enums.StatusCodeEnum;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import cn.hutool.core.util.StrUtil;
import pub.module.dating.api.constants.CusMemberTierConstants;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.trade.api.service.ApiTdOrderService;

import pub.module.dating.crud.entity.DtCustomer;
import pub.module.dating.crud.service.DtCustomerService;

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
public class MgtDtCustomerController {

    @Resource
    DtCustomerService customerService;
    @Resource
    ApiDtCustomerService apiDtCustomerService;
    @Resource
    ApiTdOrderService apiTdOrderService;
    @Resource
    ApiSysUserService apiSysUserService;

    @Operation(summary = "管理端-客户分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<DtCustomer>> queryPageList(DtCustomer customer, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DtCustomer> queryWrapper = WebQueryUtil.buildQuery(customer);
        if (customer.getCusAssignSalesTimeRangeArray() != null) {
            queryWrapper.ge("DATE(cus_assign_sales_time)", customer.getCusAssignSalesTimeRangeArray()[0]);
            queryWrapper.le("DATE(cus_assign_sales_time)", customer.getCusAssignSalesTimeRangeArray()[1]);
        }
        Page<DtCustomer> page = new Page<>(pageNo, pageSize);
        IPage<DtCustomer> pageList = customerService.page(page, queryWrapper);
        fillReferrerInfo(pageList.getRecords());
        return Result.ok(pageList);
    }

    /**
     * 为客户列表冗余填充推荐人用户编码、推荐人用户姓名。
     * 客户 cusUserCode -> 用户 userReferenceUserCode（推荐人用户编码）-> 推荐人 userRealName（推荐人用户姓名）。
     */
    private void fillReferrerInfo(List<DtCustomer> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<String, UserDTO> userCache = new HashMap<>();
        for (DtCustomer customer : records) {
            String cusUserCode = StrUtil.trimToNull(customer.getCusUserCode());
            if (cusUserCode == null) {
                continue;
            }
            UserDTO cusUser = resolveUser(userCache, cusUserCode);
            if (cusUser == null) {
                continue;
            }
            String referrerUserCode = StrUtil.trimToNull(cusUser.getUserReferenceUserCode());
            if (referrerUserCode == null) {
                continue;
            }
            customer.setCusReferrerUserCode(referrerUserCode);
            UserDTO referrer = resolveUser(userCache, referrerUserCode);
            if (referrer != null) {
                customer.setCusReferrerUserName(referrer.getUserRealName());
            }
        }
    }

    private UserDTO resolveUser(Map<String, UserDTO> userCache, String userCode) {
        if (userCache.containsKey(userCode)) {
            return userCache.get(userCode);
        }
        UserDTO user = null;
        try {
            user = apiSysUserService.getUserByUserCode(userCode);
        } catch (Exception e) {
            log.warn("查询用户信息失败，userCode={}", userCode, e);
        }
        userCache.put(userCode, user);
        return user;
    }
    @Operation(summary = "管理端-客户推送成功")
    @PostMapping(value = "/push")
    public Result<String> push(@RequestBody Map<String,Object> DtCustomer) {
        apiDtCustomerService.importData(DtCustomer);
        return Result.ok("推送成功！");
    }

    @Operation(summary = "管理端-客户添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody DtCustomer customer) {
        customerService.save(customer);
        apiDtCustomerService.syncUserRealNameFromCustomer(customer.getCusUserCode(), customer.getCusName());
        notifyDatingRedundantSync(customer.getCusUserCode());
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-客户编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody DtCustomer customer) {
        customerService.updateById(customer);
        apiDtCustomerService.syncUserRealNameFromCustomer(customer.getCusUserCode(), customer.getCusName());
        notifyDatingRedundantSync(customer.getCusUserCode());
        return Result.ok("编辑成功!");
    }

    private void notifyDatingRedundantSync(String cusUserCode) {
        if (StrUtil.isNotBlank(cusUserCode)) {
            apiDtCustomerService.notifyCustomerProfileUpdated(cusUserCode.trim());
        }
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
            UpdateWrapper<DtCustomer> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(DtCustomer::getId, id);
            updateWrapper.lambda().set(DtCustomer::getCusPoolStatusCode, StatusCodeEnum.YES);
            this.customerService.update(updateWrapper);
        }
        return Result.ok("批量入库成功!");
    }

    @Operation(summary = "管理端-客户通过id查询")
    @GetMapping(value = "/queryById")
    public Result<DtCustomer> queryById(@RequestParam(name = "id") String id) {
        DtCustomer customer = customerService.getById(id);
        return Result.ok(customer);
    }

    @Operation(summary = "管理端-客户赠送会员")
    @PostMapping(value = "/giftMember")
    public Result<String> giftMember(@RequestBody Map<String, Object> body) {
        Object idObj = body == null ? null : body.get("id");
        if (idObj == null || StrUtil.isBlank(String.valueOf(idObj))) {
            return Result.error("客户id不能为空");
        }
        DtCustomer customer = customerService.getById(String.valueOf(idObj));
        if (customer == null) {
            return Result.error("客户不存在");
        }
        String userCode = customer.getCusUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("该客户未绑定用户，无法赠送会员");
        }
        // 与用户端主动下单一致，商品编码为 freevip，直接跳过付费流程完成
        apiTdOrderService.createPaidOrder(
                CusMemberTierConstants.FREE_VIP,
                BigDecimal.ONE,
                userCode.trim(),
                customer.getCusName(),
                customer.getCusPhone());
        return Result.ok("赠送会员成功！");
    }

}