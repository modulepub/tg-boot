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
import pub.module.ai.crud.entity.AiAgent;
import pub.module.ai.crud.service.IAiAgentService;

import java.util.Collection;

@Tag(name = "管理端-ai_agent")
@RestController
@RequestMapping("/mgt/ai/aiAgent")
public class MgtAiAgentController {

    @Resource
    private IAiAgentService aiAgentService;

    @Operation(summary = "管理端-ai_agent-分页列表查询")
    @GetMapping("/list")
    public Result<IPage<AiAgent>> queryPageList(AiAgent entity,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<AiAgent> queryWrapper = WebQueryUtil.buildQuery(entity);
        Page<AiAgent> page = new Page<>(pageNo, pageSize);
        return Result.ok(aiAgentService.page(page, queryWrapper));
    }

    @Operation(summary = "管理端-ai_agent-新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody AiAgent entity) {
        aiAgentService.save(entity);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-ai_agent-编辑")
    @RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
    public Result<String> edit(@RequestBody AiAgent entity) {
        aiAgentService.updateById(entity);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-ai_agent-批量删除")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        aiAgentService.removeByBizCodes(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-ai_agent-按业务编码查询")
    @GetMapping("/queryById")
    public Result<AiAgent> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(aiAgentService.getByCode(id));
    }
}
