package pub.module.affines.biz.controller.cus;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.affines.api.service.ApiAfChildProfileViewService;
import pub.module.affines.api.service.dto.AfChildProfileViewDTO;
import pub.module.common.model.vo.Result;
import pub.module.system.api.util.UserUtil;

import java.util.List;

@Tag(name = "用户端-孩子资料卡浏览记录")
@RestController
@RequestMapping("/cus/affines/afChildProfileView")
@Slf4j
public class CusAfChildProfileViewController {

    @Resource
    private ApiAfChildProfileViewService apiAfChildProfileViewService;

    @Operation(summary = "用户端-记录浏览")
    @PostMapping("/record")
    public Result<String> record(@RequestParam String afChildProfileCode) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        apiAfChildProfileViewService.recordView(userCode, afChildProfileCode);
        return Result.ok("已记录");
    }

    @Operation(summary = "用户端-我的浏览记录")
    @GetMapping("/myList")
    public Result<List<AfChildProfileViewDTO>> myList() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        return Result.ok(apiAfChildProfileViewService.listMyViews(userCode));
    }
}
