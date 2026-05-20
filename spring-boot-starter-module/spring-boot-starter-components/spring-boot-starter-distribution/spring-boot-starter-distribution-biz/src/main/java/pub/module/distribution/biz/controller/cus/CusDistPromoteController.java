package pub.module.distribution.biz.controller.cus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.distribution.api.constants.DistBizLineCodeEnum;
import pub.module.distribution.api.service.ApiDistPromoteService;
import pub.module.distribution.api.service.ApiDistWalletService;
import pub.module.distribution.api.service.dto.DistPromoteInviteeRowDTO;
import pub.module.distribution.api.service.dto.DistPromoteSummaryDTO;
import pub.module.distribution.api.service.dto.WalWithdrawApplyDTO;
import pub.module.distribution.api.service.dto.WalWithdrawRecordDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

import java.util.List;

@Tag(name = "用户端-推广分佣")
@RestController
@RequestMapping("/cus/distribution/promote")
public class CusDistPromoteController {

    @Resource
    private ApiDistPromoteService apiDistPromoteService;
    @Resource
    private ApiDistWalletService apiDistWalletService;

    @Operation(summary = "推广页汇总")
    @GetMapping("/summary")
    public Result<DistPromoteSummaryDTO> summary(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        return Result.ok(apiDistPromoteService.getSummary(user.getUserCode(), bizLine));
    }

    @Operation(summary = "推广明细列表")
    @GetMapping("/invitees")
    public Result<List<DistPromoteInviteeRowDTO>> invitees(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        return Result.ok(apiDistPromoteService.listInvitees(user.getUserCode(), bizLine));
    }

    @Operation(summary = "申请提现")
    @PostMapping("/withdraw")
    public Result<String> withdraw(@RequestBody WalWithdrawApplyDTO dto,
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        apiDistWalletService.applyWithdraw(user.getUserCode(), bizLine, dto);
        return Result.ok("提现申请已提交");
    }

    @Operation(summary = "提现记录")
    @GetMapping("/withdrawRecords")
    public Result<List<WalWithdrawRecordDTO>> withdrawRecords(
            @RequestParam(value = "distBizLineCode", required = false) String distBizLineCode) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String bizLine = distBizLineCode != null ? distBizLineCode : DistBizLineCodeEnum.DATING.getCode();
        return Result.ok(apiDistWalletService.listWithdrawRecords(user.getUserCode(), bizLine));
    }
}
