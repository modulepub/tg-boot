package pub.module.dict.biz.controller.pub;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dict.api.service.BizDictService;
import pub.module.common.model.vo.Result;

import jakarta.annotation.Resource;

import java.util.List;

@Tag(name = "公开-字典服务")
@RestController
@RequestMapping("/pub/dict")
@Slf4j
public class PubDictController {
    @Resource
    private BizDictService bizDictService;


    @Operation(summary = "公开-前端翻译-获取所有字典")
    @GetMapping(value = "/listAll")
    public Result<List<BizDictService.DictDTO>> listAll(@Schema(description = "取多个字典以逗号分隔")
                                                        @RequestParam(name = "dictCode", required = false) String dictCode) {
        if (StrUtil.isEmpty(dictCode)) {
            dictCode = "all";
        }
        return Result.ok(bizDictService.listByCode(dictCode));
    }

}
