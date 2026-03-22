package pub.module.customer.biz.controller.mgt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.customer.api.constants.CusDealtStatusCodeEnum;
import pub.module.customer.api.constants.CusFollowUpStatusCodeEnum;
import pub.module.customer.api.constants.CusIntentionStatusCodeEnum;
import pub.module.customer.curd.entity.CustomerContactRecord;
import pub.module.customer.curd.entity.CustomerPromotionTask;
import pub.module.customer.curd.service.CustomerContactRecordService;
import pub.module.customer.curd.service.CustomerPromotionTaskService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.vo.Result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


/**
 * 客户 Controller
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "客户 CURD 处理器")
@RestController
@RequestMapping("/mgt/customer/dashboard")
@Slf4j
public class MgtCustomerEmDashboardController {

    @Resource
    ApiSysUserService apiSysUserService;
    @Resource
    CustomerPromotionTaskService customerPromotionTaskService;
    @Resource
    CustomerContactRecordService customerContactRecordService;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class SalesPerformanceResVO extends UserDTO {
        @Schema(description = "总数")
        long cusSalesTotal;
        @Schema(description = "已跟进")
        long cusFollowUpTotal;
        @Schema(description = "已成交总数")
        long cusSalesDealtTotal;
        @Schema(description = "跟进率")
        BigDecimal cusFollowUpRate;
        @Schema(description = "成交率")
        BigDecimal cusSalesDealtRate;
        @Schema(description = "总电话数")
        BigDecimal cusContactRecordTotal;
        @Schema(description = "意向总数")
        long cusIntentionTotal;
        @Schema(description = "意向率")
        BigDecimal cusIntentionRate;
        @Schema(description = "接听数")
        long cusAnswerTotal;
        @Schema(description = "接听率")
        BigDecimal cusAnswerRate;
        @Schema(description = "有效通话数")
        long cusEffectiveCallDurationTotal;
        @Schema(description = "有效通话率")
        BigDecimal cusEffectiveCallDurationRate;
    }

    @Data
    public static class SalesPerformanceReqVO {
        @Schema(description = "创建日期检索")
        private String[] createDateRangeArray;
    }

    @Operation(summary = "客户 - 分页列表查询")
    @GetMapping(value = "/salesPerformanceList")
    public Result<IPage<SalesPerformanceResVO>> salesPerformanceList(SalesPerformanceReqVO salesPerformanceReqVO) {
        Assert.notNull(salesPerformanceReqVO.getCreateDateRangeArray());
        List<SalesPerformanceResVO> salesPerformanceResVOList = new ArrayList<>();
        List<UserDTO> list = apiSysUserService.list(new UserDTO());
        for (UserDTO userDTO : list) {
            SalesPerformanceResVO salesPerformanceListVO = BeanUtil.copyProperties(userDTO, SalesPerformanceResVO.class);
            long cusSalesTotal = customerPromotionTaskService.count(
                    new QueryWrapper<CustomerPromotionTask>()
                            .eq("user_code", userDTO.getUserCode())
                            .ge("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[1])
            );
            salesPerformanceListVO.setCusSalesTotal(cusSalesTotal);
            long cusFollowUpTotal = customerPromotionTaskService.count(
                    new QueryWrapper<CustomerPromotionTask>()
                            .eq("user_code", userDTO.getUserCode())
                            .eq("cus_follow_up_status_code", CusFollowUpStatusCodeEnum.YES.getCode())
                            .ge("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[1])
            );
            salesPerformanceListVO.setCusFollowUpTotal(cusFollowUpTotal);
            long cusSalesDealtTotal = customerPromotionTaskService.count(
                    new QueryWrapper<CustomerPromotionTask>()
                            .eq("user_code", userDTO.getUserCode())
                            .eq("cus_dealt_status_code", CusDealtStatusCodeEnum.YES.getCode())
                            .ge("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[1])
            );
            salesPerformanceListVO.setCusSalesDealtTotal(cusSalesDealtTotal);

            long cusIntentionTotal = customerContactRecordService.count(
                    new QueryWrapper<CustomerContactRecord>()
                            .eq("user_code", userDTO.getUserCode())
                            .eq("cus_intention_status_code", CusIntentionStatusCodeEnum.YES.getCode())
                            .ge("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[1])
            );
            salesPerformanceListVO.setCusIntentionTotal(cusIntentionTotal);

            long cusAnswerTotal = customerContactRecordService.count(
                    new QueryWrapper<CustomerContactRecord>()
                            .eq("user_code", userDTO.getUserCode())
                            .gt("contact_record_talk_duration", 0)
                            .ge("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[1])
            );
            salesPerformanceListVO.setCusAnswerTotal(cusAnswerTotal);
            long cusEffectiveCallDurationTotal = customerContactRecordService.count(
                    new QueryWrapper<CustomerContactRecord>()
                            .eq("user_code", userDTO.getUserCode())
                            .gt("contact_record_talk_duration", 10)
                            .ge("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[0])
                            .le("DATE(create_time)", salesPerformanceReqVO.getCreateDateRangeArray()[1])
            );
            salesPerformanceListVO.setCusEffectiveCallDurationTotal(cusEffectiveCallDurationTotal);


            if (cusSalesTotal > 0) {
                BigDecimal cusFollowUpRate = new BigDecimal(cusFollowUpTotal).divide(new BigDecimal(cusSalesTotal), 2, RoundingMode.HALF_UP);
                salesPerformanceListVO.setCusFollowUpRate(cusFollowUpRate);
            }
            if (cusFollowUpTotal > 0) {
                BigDecimal cusSalesDealtRate = new BigDecimal(cusSalesDealtTotal).divide(new BigDecimal(cusFollowUpTotal), 2, RoundingMode.HALF_UP);
                salesPerformanceListVO.setCusSalesDealtRate(cusSalesDealtRate);
            }
            if (cusFollowUpTotal > 0) {
                BigDecimal cusIntentionRate = new BigDecimal(cusIntentionTotal).divide(new BigDecimal(cusFollowUpTotal), 2, RoundingMode.HALF_UP);
                salesPerformanceListVO.setCusIntentionRate(cusIntentionRate);
            }
            if (cusFollowUpTotal > 0) {
                BigDecimal cusAnswerRate = new BigDecimal(cusAnswerTotal).divide(new BigDecimal(cusFollowUpTotal), 2, RoundingMode.HALF_UP);
                salesPerformanceListVO.setCusAnswerRate(cusAnswerRate);
            }

            if (cusFollowUpTotal > 0) {
                BigDecimal cusEffectiveCallDurationRate = new BigDecimal(cusEffectiveCallDurationTotal).divide(new BigDecimal(cusFollowUpTotal), 2, RoundingMode.HALF_UP);
                salesPerformanceListVO.setCusEffectiveCallDurationRate(cusEffectiveCallDurationRate);
            }


            salesPerformanceResVOList.add(salesPerformanceListVO);
        }
        IPage<SalesPerformanceResVO> page = new Page<>(1, salesPerformanceResVOList.size());
        page.setTotal(salesPerformanceResVOList.size());
        page.setRecords(salesPerformanceResVOList);
        return Result.ok(page);
    }


}