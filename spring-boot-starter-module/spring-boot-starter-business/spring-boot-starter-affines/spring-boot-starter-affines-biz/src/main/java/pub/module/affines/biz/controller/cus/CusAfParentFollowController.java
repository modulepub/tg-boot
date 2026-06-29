package pub.module.affines.biz.controller.cus;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.affines.api.service.ApiAfParentFollowService;
import pub.module.affines.api.service.dto.AfParentFollowDTO;
import pub.module.affines.api.service.dto.AfParentFollowRequestDTO;
import pub.module.common.model.vo.Result;
import pub.module.system.api.util.UserUtil;

import java.util.List;

@Tag(name = "用户端-家长关注")
@RestController
@RequestMapping("/cus/affines/afParentFollow")
@Slf4j
public class CusAfParentFollowController {

    @Resource
    private ApiAfParentFollowService apiAfParentFollowService;

    @Operation(summary = "用户端-关注资料卡")
    @PostMapping("/follow")
    public Result<String> follow(@RequestBody AfParentFollowRequestDTO dto) {
        Assert.notNull(dto, "参数不能为空");
        Assert.notBlank(dto.getAfTargetChildProfileCode(), "资料卡编码不能为空");
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        apiAfParentFollowService.follow(userCode, dto.getAfTargetChildProfileCode().trim());
        return Result.ok("关注成功");
    }

    @Operation(summary = "用户端-取消关注")
    @PostMapping("/unfollow")
    public Result<String> unfollow(@RequestBody AfParentFollowRequestDTO dto) {
        Assert.notNull(dto, "参数不能为空");
        Assert.notBlank(dto.getAfTargetChildProfileCode(), "资料卡编码不能为空");
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        apiAfParentFollowService.unfollow(userCode, dto.getAfTargetChildProfileCode().trim());
        return Result.ok("已取消关注");
    }

    @Operation(summary = "用户端-是否已关注")
    @GetMapping("/followStatus")
    public Result<Boolean> followStatus(@RequestParam String afTargetChildProfileCode) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        return Result.ok(apiAfParentFollowService.isFollowing(userCode, afTargetChildProfileCode));
    }

    @Operation(summary = "用户端-我的关注列表")
    @GetMapping("/myList")
    public Result<List<AfParentFollowDTO>> myList() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        return Result.ok(apiAfParentFollowService.listMyFollows(userCode));
    }
}
