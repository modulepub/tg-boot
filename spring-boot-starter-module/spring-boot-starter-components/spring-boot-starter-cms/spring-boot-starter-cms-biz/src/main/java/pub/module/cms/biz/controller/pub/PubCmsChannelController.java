package pub.module.cms.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pub.module.cms.api.service.BizCmsService;
import pub.module.web.vo.Result;
import pub.module.cms.curd.entity.CmsChannel;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CMS门户端接口")
@RestController
@RequestMapping("/pub/cms/channel")
@Slf4j
@AllArgsConstructor
public class PubCmsChannelController {

    private BizCmsService bizCmsService;

    @Operation(summary = "获取栏目完整树状结构")
    @GetMapping("/getTree")
    public Result<CmsChannel> getTree(@RequestParam(name = "ccCode") String ccCode) {
        return Result.ok(bizCmsService.getTree(ccCode));
    }


}
