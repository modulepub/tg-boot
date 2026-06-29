package pub.module.ai.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.ai.crud.entity.AiUsageRecord;
import pub.module.ai.crud.service.IAiUsageRecordService;

@Tag(name = "管理端-ai_usage_record")
@RestController
@RequestMapping("/mgt/ai/aiUsageRecord")
public class MgtAiUsageRecordController {

    @Resource
    private IAiUsageRecordService aiUsageRecordService;

    @Operation(summary = "管理端-ai_usage_record-分页列表查询")
    @GetMapping("/list")
    public Result<IPage<AiUsageRecord>> queryPageList(AiUsageRecord entity,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<AiUsageRecord> queryWrapper = WebQueryUtil.buildQuery(entity);
        queryWrapper.orderByDesc("create_time");
        Page<AiUsageRecord> page = new Page<>(pageNo, pageSize);
        return Result.ok(aiUsageRecordService.page(page, queryWrapper));
    }

    @Operation(summary = "管理端-ai_usage_record-按业务编码查询")
    @GetMapping("/queryById")
    public Result<AiUsageRecord> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(aiUsageRecordService.getByCode(id));
    }
}
