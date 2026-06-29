package pub.module.system.biz.controller.cus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.system.api.service.ApiSysUserCancellationService;
import pub.module.system.api.service.dto.SysUserCancellationApplyDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

/**
 * 用户端-账号注销申请
 */
@Tag(name = "用户端-账号注销")
@RestController
@RequestMapping("/cus/system/sysUserCancellation")
@Slf4j
public class CusSysUserCancellationController {

    @Resource
    private ApiSysUserCancellationService apiSysUserCancellationService;

    @Operation(summary = "用户端-提交账号注销申请")
    @PostMapping("/submitApply")
    public Result<SysUserCancellationApplyDTO> submitApply() {
        UserDTO user = UserUtil.getCurrentSysUser();
        SysUserCancellationApplyDTO apply = apiSysUserCancellationService.submitApply(user.getUserCode());
        return Result.ok("注销申请已提交，预计 7 个工作日内完成处理", apply);
    }

    @Operation(summary = "用户端-查询当前用户最新注销申请")
    @GetMapping("/getMyApply")
    public Result<SysUserCancellationApplyDTO> getMyApply() {
        UserDTO user = UserUtil.getCurrentSysUser();
        SysUserCancellationApplyDTO apply = apiSysUserCancellationService.getLatestByUserCode(user.getUserCode());
        return Result.ok(apply);
    }
}
