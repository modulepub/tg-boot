package pub.module.verification.biz.controller.mgt;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.verification.api.dto.ContentModerationRejectVO;
import pub.module.verification.api.service.ApiContentModerationMgtService;
import pub.module.verification.crud.entity.CmRecord;
import pub.module.verification.crud.service.CmRecordService;

import java.util.Collection;

@Tag(name = "管理端-内容合法校验")
@RestController
@RequestMapping("/mgt/verification/cmRecord")
@Slf4j
public class MgtCmRecordController {

    @Resource
    private CmRecordService cmRecordService;
    @Resource
    private ApiContentModerationMgtService apiContentModerationMgtService;

    @Operation(summary = "管理端-内容审核记录分页列表")
    @GetMapping("/list")
    public Result<IPage<CmRecord>> queryPageList(
            CmRecord query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        // 姓名走模糊匹配：先摘出避免被通用构造器按精确 eq 处理
        String userName = StrUtil.trim(query.getCmRecordUserName());
        query.setCmRecordUserName(null);
        QueryWrapper<CmRecord> queryWrapper = WebQueryUtil.buildQuery(query);
        if (StrUtil.isNotBlank(userName)) {
            queryWrapper.like("cm_record_user_name", userName);
        }
        queryWrapper.orderByDesc("create_time");
        Page<CmRecord> page = new Page<>(pageNo, pageSize);
        return Result.ok(cmRecordService.page(page, queryWrapper));
    }

    @Operation(summary = "管理端-内容审核记录详情")
    @GetMapping("/queryById")
    public Result<CmRecord> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(cmRecordService.getById(id));
    }

    @Operation(summary = "管理端-人工审核通过")
    @PostMapping("/approve")
    public Result<String> approve(@RequestParam(name = "id") String id) {
        apiContentModerationMgtService.approve(id, currentAuditBy());
        return Result.ok("审核通过");
    }

    @Operation(summary = "管理端-人工审核驳回")
    @PostMapping("/reject")
    public Result<String> reject(@RequestBody ContentModerationRejectVO vo) {
        apiContentModerationMgtService.reject(vo.getId(), vo.getRejectReason(), currentAuditBy());
        return Result.ok("已驳回");
    }

    @Operation(summary = "管理端-内容审核记录批量删除（按业务编码 cmRecordCode）")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> cmRecordCodes) {
        cmRecordService.removeByBizCodes(cmRecordCodes);
        return Result.ok("批量删除成功!");
    }

    private static String currentAuditBy() {
        try {
            UserDTO user = UserUtil.getCurrentSysUser();
            return user != null ? user.getUserCode() : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
