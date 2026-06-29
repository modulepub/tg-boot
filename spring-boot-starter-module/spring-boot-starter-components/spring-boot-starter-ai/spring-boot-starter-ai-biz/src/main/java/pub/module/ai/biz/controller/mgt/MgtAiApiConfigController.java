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
import pub.module.ai.crud.entity.AiApiConfig;
import pub.module.ai.crud.service.IAiApiConfigService;

import java.util.Collection;

@Tag(name = "管理端-ai_api_config")
@RestController
@RequestMapping("/mgt/ai/aiApiConfig")
public class MgtAiApiConfigController {

    @Resource
    private IAiApiConfigService aiApiConfigService;

    @Operation(summary = "管理端-ai_api_config-分页列表查询")
    @GetMapping("/list")
    public Result<IPage<AiApiConfig>> queryPageList(AiApiConfig entity,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<AiApiConfig> queryWrapper = WebQueryUtil.buildQuery(entity);
        Page<AiApiConfig> page = new Page<>(pageNo, pageSize);
        IPage<AiApiConfig> pageList = aiApiConfigService.page(page, queryWrapper);
        pageList.getRecords().forEach(this::maskApiKey);
        return Result.ok(pageList);
    }

    @Operation(summary = "管理端-ai_api_config-新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody AiApiConfig entity) {
        aiApiConfigService.save(entity);
        return Result.ok("添加成功！");
    }

    @Operation(summary = "管理端-ai_api_config-编辑")
    @RequestMapping(value = "/edit", method = { RequestMethod.PUT, RequestMethod.POST })
    public Result<String> edit(@RequestBody AiApiConfig entity) {
        aiApiConfigService.updateById(entity);
        return Result.ok("编辑成功!");
    }

    @Operation(summary = "管理端-ai_api_config-批量删除")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        aiApiConfigService.removeByBizCodes(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-ai_api_config-按业务编码查询")
    @GetMapping("/queryById")
    public Result<AiApiConfig> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(aiApiConfigService.getByCode(id));
    }

    private void maskApiKey(AiApiConfig entity) {
        String key = entity.getAiApiConfigApiKey();
        if (key != null && key.length() > 8) {
            entity.setAiApiConfigApiKey(key.substring(0, 4) + "****" + key.substring(key.length() - 4));
        }
    }
}
