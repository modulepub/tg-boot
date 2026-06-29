package pub.module.affines.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.affines.crud.entity.AfParentFollow;
import pub.module.affines.crud.service.AfParentFollowService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import java.util.Collection;

@Tag(name = "管理端-家长关注")
@RestController
@RequestMapping("/mgt/affines/afParentFollow")
@Slf4j
public class MgtAfParentFollowController {

    @Resource
    private AfParentFollowService afParentFollowService;

    @Operation(summary = "管理端-家长关注分页列表")
    @GetMapping("/list")
    public Result<IPage<AfParentFollow>> list(AfParentFollow query,
                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<AfParentFollow> wrapper = WebQueryUtil.buildQuery(query);
        IPage<AfParentFollow> page = afParentFollowService.page(new Page<>(pageNo, pageSize), wrapper);
        return Result.ok(page);
    }

    @Operation(summary = "管理端-家长关注批量删除")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Collection<String> afParentFollowCodes) {
        afParentFollowService.removeByBizCodes(afParentFollowCodes);
        return Result.ok("删除成功");
    }
}
