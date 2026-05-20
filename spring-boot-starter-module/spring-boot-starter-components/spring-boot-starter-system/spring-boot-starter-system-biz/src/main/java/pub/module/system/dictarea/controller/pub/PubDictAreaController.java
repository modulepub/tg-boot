package pub.module.system.dictarea.controller.pub;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.system.dictarea.entity.DictArea;
import pub.module.system.dictarea.service.DictAreaService;

/**
 * 公开地区字典（生活城市、国际地区下拉与搜索）
 */
@Tag(name = "公开-地区字典")
@RestController
@RequestMapping("/pub/dict/area")
public class PubDictAreaController {

    @Resource
    private DictAreaService dictAreaService;

    @Operation(summary = "公开-地区分页检索（支持关键词模糊搜索或按父级展开）")
    @GetMapping("/search")
    public Result<IPage<DictArea>> search(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "parentCode", required = false) String parentCode,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        return Result.ok(dictAreaService.searchArea(keyword, parentCode, pageNo, pageSize));
    }
}
