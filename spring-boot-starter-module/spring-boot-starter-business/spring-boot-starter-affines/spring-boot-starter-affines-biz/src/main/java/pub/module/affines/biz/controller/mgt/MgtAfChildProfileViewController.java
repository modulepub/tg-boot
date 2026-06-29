package pub.module.affines.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.affines.crud.entity.AfChildProfileView;
import pub.module.affines.crud.service.AfChildProfileViewService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import java.util.Collection;

@Tag(name = "管理端-资料卡浏览记录")
@RestController
@RequestMapping("/mgt/affines/afChildProfileView")
@Slf4j
public class MgtAfChildProfileViewController {

    @Resource
    private AfChildProfileViewService afChildProfileViewService;

    @Operation(summary = "管理端-浏览记录分页列表")
    @GetMapping("/list")
    public Result<IPage<AfChildProfileView>> list(AfChildProfileView query,
                                                  @RequestParam(defaultValue = "1") Integer pageNo,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<AfChildProfileView> wrapper = WebQueryUtil.buildQuery(query);
        IPage<AfChildProfileView> page = afChildProfileViewService.page(new Page<>(pageNo, pageSize), wrapper);
        return Result.ok(page);
    }

    @Operation(summary = "管理端-浏览记录批量删除")
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Collection<String> afChildProfileViewCodes) {
        afChildProfileViewService.removeByBizCodes(afChildProfileViewCodes);
        return Result.ok("删除成功");
    }
}
