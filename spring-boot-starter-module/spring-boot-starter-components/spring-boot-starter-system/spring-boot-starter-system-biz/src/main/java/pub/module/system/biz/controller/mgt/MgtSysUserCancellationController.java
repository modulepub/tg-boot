package pub.module.system.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.api.service.ApiSysUserCancellationMgtService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.system.crud.entity.SysUserCancellationApply;
import pub.module.system.crud.service.SysUserCancellationApplyService;

/**
 * 管理端-账号注销申请
 */
@Tag(name = "管理端-账号注销")
@RestController
@RequestMapping("/mgt/system/sysUserCancellation")
@Slf4j
public class MgtSysUserCancellationController {

    @Resource
    private SysUserCancellationApplyService sysUserCancellationApplyService;
    @Resource
    private ApiSysUserCancellationMgtService apiSysUserCancellationMgtService;

    @Operation(summary = "管理端-账号注销申请分页列表")
    @GetMapping("/list")
    public Result<IPage<SysUserCancellationApply>> queryPageList(
            SysUserCancellationApply sysUserCancellationApply,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SysUserCancellationApply> queryWrapper = WebQueryUtil.buildQuery(sysUserCancellationApply);
        queryWrapper.orderByDesc("create_time");
        Page<SysUserCancellationApply> page = new Page<>(pageNo, pageSize);
        IPage<SysUserCancellationApply> pageList = sysUserCancellationApplyService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-账号注销申请详情")
    @GetMapping("/queryById")
    public Result<SysUserCancellationApply> queryById(@RequestParam(name = "id") String id) {
        SysUserCancellationApply apply = sysUserCancellationApplyService.getById(id);
        return Result.ok(apply);
    }

    @Operation(summary = "管理端-标记注销申请为已处理")
    @PostMapping("/process")
    public Result<String> process(@RequestParam("id") String id) {
        UserDTO user = UserUtil.getCurrentSysUser();
        String processBy = user != null ? user.getUserCode() : null;
        apiSysUserCancellationMgtService.process(id, processBy);
        return Result.ok("已处理，账号已注销");
    }
}
