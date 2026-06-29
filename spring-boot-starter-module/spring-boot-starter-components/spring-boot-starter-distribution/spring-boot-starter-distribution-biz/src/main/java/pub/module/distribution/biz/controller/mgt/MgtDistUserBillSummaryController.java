package pub.module.distribution.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.distribution.crud.entity.DistUserBillSummary;
import pub.module.distribution.crud.mapper.DistUserBillSummaryMapper;

@Tag(name = "管理端-用户账单汇总")
@RestController
@RequestMapping("/mgt/distribution/distUserBillSummary")
public class MgtDistUserBillSummaryController {

    @Resource
    private DistUserBillSummaryMapper distUserBillSummaryMapper;

    @Operation(summary = "分页列表")
    @GetMapping("/list")
    public Result<IPage<DistUserBillSummary>> list(DistUserBillSummary query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DistUserBillSummary> wrapper = WebQueryUtil.buildQuery(query);
        wrapper.lambda().orderByDesc(DistUserBillSummary::getCreateTime);
        return Result.ok(distUserBillSummaryMapper.selectPage(new Page<>(pageNo, pageSize), wrapper));
    }

    @Operation(summary = "按主键查询")
    @GetMapping("/queryById")
    public Result<DistUserBillSummary> queryById(@RequestParam("id") String id) {
        return Result.ok(distUserBillSummaryMapper.selectById(id));
    }
}
