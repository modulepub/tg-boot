package pub.module.affines.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.affines.crud.entity.AfChildProfile;
import pub.module.affines.crud.service.AfChildProfileService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import java.util.Collection;

@Tag(name = "管理端-孩子资料卡")
@RestController
@RequestMapping("/mgt/affines/afChildProfile")
@Slf4j
public class MgtAfChildProfileController {

    @Resource
    private AfChildProfileService afChildProfileService;

    @Operation(summary = "管理端-孩子资料卡分页列表")
    @GetMapping("/list")
    public Result<IPage<AfChildProfile>> list(AfChildProfile query,
                                              @RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<AfChildProfile> wrapper = WebQueryUtil.buildQuery(query);
        IPage<AfChildProfile> page = afChildProfileService.page(new Page<>(pageNo, pageSize), wrapper);
        return Result.ok(page);
    }

    @Operation(summary = "管理端-孩子资料卡详情")
    @GetMapping("/queryById")
    public Result<AfChildProfile> queryById(@RequestParam String id) {
        return Result.ok(afChildProfileService.getById(id));
    }

    @Operation(summary = "管理端-孩子资料卡编辑")
    @PostMapping("/edit")
    public Result<String> edit(@RequestBody AfChildProfile body) {
        afChildProfileService.updateById(body);
        return Result.ok("编辑成功");
    }

    @Operation(summary = "管理端-孩子资料卡批量删除")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Collection<String> afChildProfileCodes) {
        afChildProfileService.removeByBizCodes(afChildProfileCodes);
        return Result.ok("删除成功");
    }
}
