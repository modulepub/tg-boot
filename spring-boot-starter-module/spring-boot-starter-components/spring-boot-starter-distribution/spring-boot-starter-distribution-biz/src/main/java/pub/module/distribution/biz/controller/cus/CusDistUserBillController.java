package pub.module.distribution.biz.controller.cus;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.service.ApiDistUserBillSummaryService;
import pub.module.distribution.api.service.dto.DistUserBillPromoteStatsDTO;
import pub.module.distribution.api.service.dto.DistUserBillSettleRecordDTO;
import pub.module.distribution.api.service.dto.DistUserBillSummaryDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

@Tag(name = "用户端-账单汇总")
@RestController
@RequestMapping("/cus/distribution/bill")
public class CusDistUserBillController {

    @Resource
    private ApiDistUserBillSummaryService apiDistUserBillSummaryService;

    @Operation(summary = "用户账单汇总")
    @GetMapping("/summary")
    public Result<DistUserBillSummaryDTO> summary(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        return Result.ok(apiDistUserBillSummaryService.getSummary(user.getUserCode(), bizLine));
    }

    @Operation(summary = "推广页顶部统计")
    @GetMapping("/promote/stats")
    public Result<DistUserBillPromoteStatsDTO> promoteStats(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        return Result.ok(apiDistUserBillSummaryService.getPromoteStats(user.getUserCode(), bizLine));
    }

    @Operation(summary = "推广明细分页（按推广人查询下级账单汇总）")
    @GetMapping("/promote/page")
    public Result<IPage<DistUserBillSummaryDTO>> promotePage(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        return Result.ok(apiDistUserBillSummaryService.pageByPromoter(
                user.getUserCode(), bizLine, pageNo, pageSize));
    }

    @Operation(summary = "推广明细-下级用户账单结算记录分页")
    @GetMapping("/settle/page")
    public Result<IPage<DistUserBillSettleRecordDTO>> settlePage(
            @RequestParam("distPayerUserCode") String distPayerUserCode,
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        return Result.ok(apiDistUserBillSummaryService.pageSettleRecordsByInvitee(
                user.getUserCode(), distPayerUserCode, bizLine, pageNo, pageSize));
    }
}
