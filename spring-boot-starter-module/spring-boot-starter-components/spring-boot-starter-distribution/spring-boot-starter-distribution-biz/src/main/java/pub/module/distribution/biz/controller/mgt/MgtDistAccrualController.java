package pub.module.distribution.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.distribution.curd.entity.DistAccrual;
import pub.module.distribution.curd.mapper.DistAccrualMapper;

@Tag(name = "管理端-分佣明细")
@RestController
@RequestMapping("/mgt/distribution/distAccrual")
public class MgtDistAccrualController {

    @Resource
    private DistAccrualMapper distAccrualMapper;

    @GetMapping("/list")
    public Result<IPage<DistAccrual>> list(DistAccrual query,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DistAccrual> wrapper = WebQueryUtil.buildQuery(query);
        wrapper.lambda().orderByDesc(DistAccrual::getCreateTime);
        return Result.ok(distAccrualMapper.selectPage(new Page<>(pageNo, pageSize), wrapper));
    }
}
