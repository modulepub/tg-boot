package pub.module.affines.biz.controller.cus;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.affines.api.service.ApiAfChildProfileService;
import pub.module.affines.api.service.dto.AfChildProfileDTO;
import pub.module.common.model.vo.Result;
import pub.module.system.api.util.UserUtil;

import java.util.List;

@Tag(name = "用户端-孩子资料卡")
@RestController
@RequestMapping("/cus/affines/afChildProfile")
@Slf4j
public class CusAfChildProfileController {

    @Resource
    private ApiAfChildProfileService apiAfChildProfileService;

    @Operation(summary = "用户端-我的孩子的资料卡列表")
    @GetMapping("/myList")
    public Result<List<AfChildProfileDTO>> myList() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        return Result.ok(apiAfChildProfileService.listByParentUserCode(userCode));
    }

    @Operation(summary = "用户端-资料卡详情")
    @GetMapping("/detail")
    public Result<AfChildProfileDTO> detail(@RequestParam String afChildProfileCode) {
        AfChildProfileDTO dto = apiAfChildProfileService.getDetailByCode(afChildProfileCode);
        if (dto == null) {
            return Result.error("资料卡不存在");
        }
        return Result.ok(dto);
    }

    @Operation(summary = "用户端-新增孩子资料卡")
    @PostMapping("/add")
    public Result<AfChildProfileDTO> add(@RequestBody AfChildProfileDTO body) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        return Result.ok(apiAfChildProfileService.saveProfile(userCode, body));
    }

    @Operation(summary = "用户端-编辑孩子资料卡")
    @PostMapping("/edit")
    public Result<AfChildProfileDTO> edit(@RequestBody AfChildProfileDTO body) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        if (StrUtil.isBlank(body.getAfChildProfileCode())) {
            return Result.error("资料卡编码不能为空");
        }
        return Result.ok(apiAfChildProfileService.updateProfile(userCode, body));
    }

    @Operation(summary = "用户端-删除孩子资料卡")
    @PostMapping("/delete")
    public Result<String> delete(@RequestParam String afChildProfileCode) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        if (StrUtil.isBlank(userCode)) {
            return Result.error("未登录");
        }
        apiAfChildProfileService.deleteProfile(userCode, afChildProfileCode);
        return Result.ok("删除成功");
    }
}
