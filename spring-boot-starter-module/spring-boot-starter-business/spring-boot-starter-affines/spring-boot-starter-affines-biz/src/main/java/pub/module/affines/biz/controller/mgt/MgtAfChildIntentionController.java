package pub.module.affines.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.affines.crud.entity.AfChildIntention;
import pub.module.affines.crud.service.AfChildIntentionService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import java.util.Collection;

@Tag(name = "管理端-孩子意向")
@RestController
@RequestMapping("/mgt/affines/afChildIntention")
@Slf4j
public class MgtAfChildIntentionController {

    @Resource
    private AfChildIntentionService afChildIntentionService;

    @Operation(summary = "管理端-孩子意向分页列表")
    @GetMapping("/list")
    public Result<IPage<AfChildIntention>> list(AfChildIntention query,
                                                @RequestParam(defaultValue = "1") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<AfChildIntention> wrapper = WebQueryUtil.buildQuery(query);
        IPage<AfChildIntention> page = afChildIntentionService.page(new Page<>(pageNo, pageSize), wrapper);
        return Result.ok(page);
    }

    @Operation(summary = "管理端-孩子意向编辑")
    @PostMapping("/edit")
    public Result<String> edit(@RequestBody AfChildIntention body) {
        afChildIntentionService.updateById(body);
        return Result.ok("编辑成功");
    }

    @Operation(summary = "管理端-孩子意向批量删除")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Collection<String> afChildIntentionCodes) {
        afChildIntentionService.removeByBizCodes(afChildIntentionCodes);
        return Result.ok("删除成功");
    }
}
