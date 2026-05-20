package pub.module.distribution.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.distribution.curd.entity.DistRule;
import pub.module.distribution.curd.mapper.DistRuleMapper;

import java.util.Collection;

@Tag(name = "管理端-分佣规则")
@RestController
@RequestMapping("/mgt/distribution/distRule")
public class MgtDistRuleController {

    @Resource
    private DistRuleMapper distRuleMapper;

    @GetMapping("/list")
    public Result<IPage<DistRule>> list(DistRule query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DistRule> wrapper = WebQueryUtil.buildQuery(query);
        return Result.ok(distRuleMapper.selectPage(new Page<>(pageNo, pageSize), wrapper));
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody DistRule entity) {
        distRuleMapper.insert(entity);
        return Result.ok("添加成功");
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody DistRule entity) {
        distRuleMapper.updateById(entity);
        return Result.ok("编辑成功");
    }

    @PostMapping("/delete")
    public Result<String> delete(@RequestBody Collection<String> list) {
        distRuleMapper.deleteBatchIds(list);
        return Result.ok("删除成功");
    }

    @GetMapping("/queryById")
    public Result<DistRule> queryById(@RequestParam("id") String id) {
        return Result.ok(distRuleMapper.selectById(id));
    }
}
