package pub.module.customer.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.customer.api.constants.PromotionTaskTypeCodeEnum;
import pub.module.customer.curd.entity.CustomerPromotionTask;
import pub.module.customer.curd.service.CustomerPromotionTaskService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.common.model.vo.Result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * 管理端-客户
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "管理端-客户")
@RestController
@RequestMapping("/mgt/customer/staff")
@Slf4j
public class MgtEmController {

    @Resource
    ApiSysUserService sysUserService;
    @Resource
    CustomerPromotionTaskService customerPromotionTaskService;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class StaffVO extends UserDTO {
        long todayContactTaskNum = 0;
    }

    @Operation(summary = "管理端-客户分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<StaffVO>> queryPageList(UserDTO userDTO, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        // 1. 构造今日开始时间：yyyy-MM-dd 00:00:00
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        // 2. 构造今日结束时间：yyyy-MM-dd 23:59:59.999
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        IPage<UserDTO> pageList = sysUserService.page(userDTO, pageNo, pageSize);
        IPage<StaffVO> resultPage = pageList.convert((item) -> {
            StaffVO staffVO = BeanUtil.copyProperties(item, StaffVO.class);
            long todayContactTaskNum = customerPromotionTaskService.count(new QueryWrapper<CustomerPromotionTask>().lambda()
                    .eq(CustomerPromotionTask::getPromotionTaskTypeCode, PromotionTaskTypeCodeEnum.CONTACT.getCode())
                    .eq(CustomerPromotionTask::getUserCode, staffVO.getUserCode())
                    .ge(CustomerPromotionTask::getCreateTime, todayStart)
                    .le(CustomerPromotionTask::getCreateTime, todayEnd)
            );
            staffVO.setTodayContactTaskNum(todayContactTaskNum);
            return staffVO;
        });
        return Result.ok(resultPage);
    }
}