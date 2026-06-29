package pub.module.system.biz.controller.cus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.system.api.service.ApiSysUserBadgeService;
import pub.module.system.api.service.dto.SysUserBadgeDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

import java.util.List;

/**
 * 用户端-角标
 */
@Tag(name = "用户端-角标")
@RestController
@RequestMapping("/cus/system/sysUserBadge")
@Slf4j
public class CusSysUserBadgeController {

    @Resource
    private ApiSysUserBadgeService apiSysUserBadgeService;

    @Operation(summary = "用户端-查询当前用户角标列表")
    @GetMapping("/listMy")
    public Result<List<SysUserBadgeDTO>> listMy() {
        UserDTO user = UserUtil.getCurrentSysUser();
        List<SysUserBadgeDTO> badges = apiSysUserBadgeService.listByUserCode(user.getUserCode());
        return Result.ok(badges);
    }

    @Operation(summary = "用户端-清零指定角标")
    @PostMapping("/clear")
    public Result<Void> clear(@RequestBody ClearBadgeRequest request) {
        UserDTO user = UserUtil.getCurrentSysUser();
        apiSysUserBadgeService.clearBadge(user.getUserCode(), request.getBadgeKey());
        return Result.ok();
    }

    @Data
    public static class ClearBadgeRequest {
        private String badgeKey;
    }
}
