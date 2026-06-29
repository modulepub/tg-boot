package pub.module.im.biz.controller.cus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.im.api.service.ApiImFriendService;
import pub.module.system.api.util.UserUtil;

import java.util.List;

@Tag(name = "用户端-IM好友")
@RestController
@RequestMapping("/cus/im/friend")
public class CusImFriendController {

    @Resource
    private ApiImFriendService apiImFriendService;

    @Operation(summary = "用户端-好友列表")
    @GetMapping("/list")
    public Result<List<?>> listFriends() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        return Result.ok(apiImFriendService.listFriends(userCode));
    }

    @Operation(summary = "用户端-删除好友")
    @PostMapping("/delete")
    public Result<String> deleteFriend(@RequestParam("friendUserCode") String friendUserCode) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        apiImFriendService.removeFriendBidirectional(userCode, friendUserCode);
        return Result.ok("删除成功");
    }
}
