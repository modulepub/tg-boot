package pub.module.distribution.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.distribution.api.service.ApiDistSettleBatchService;
import pub.module.distribution.crud.entity.DistSettleBatch;
import pub.module.distribution.crud.mapper.DistSettleBatchMapper;

@Tag(name = "管理端-结算批次")
@RestController
@RequestMapping("/mgt/distribution/distSettleBatch")
public class MgtDistSettleBatchController {

    @Resource
    private DistSettleBatchMapper distSettleBatchMapper;
    @Resource
    private ApiDistSettleBatchService apiDistSettleBatchService;

    @Operation(summary = "分页列表")
    @GetMapping("/list")
    public Result<IPage<DistSettleBatch>> list(DistSettleBatch query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DistSettleBatch> wrapper = WebQueryUtil.buildQuery(query);
        wrapper.lambda().orderByDesc(DistSettleBatch::getDistApplyAt);
        return Result.ok(distSettleBatchMapper.selectPage(new Page<>(pageNo, pageSize), wrapper));
    }

    @Operation(summary = "按主键查询")
    @GetMapping("/queryById")
    public Result<DistSettleBatch> queryById(@RequestParam("id") String id) {
        return Result.ok(distSettleBatchMapper.selectById(id));
    }

    @Operation(summary = "标记结算完成")
    @PostMapping("/complete")
    public Result<String> complete(@RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "distSettleBatchCode", required = false) String distSettleBatchCode) {
        apiDistSettleBatchService.complete(id, distSettleBatchCode);
        return Result.ok("操作成功");
    }
}
