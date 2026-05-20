package pub.module.cms.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.cms.api.dto.CmsShortUrlCreateReq;
import pub.module.cms.api.dto.CmsShortUrlResolveVO;
import pub.module.cms.biz.service.SpiCmsShortUrlService;
import pub.module.common.model.vo.Result;

@Tag(name = "公开-CMS短链")
@RestController
@RequestMapping("/pub/cms/cmsShortUrl")
@Slf4j
public class PubCmsShortUrlController {

    @Resource
    private SpiCmsShortUrlService spiCmsShortUrlService;

    @Operation(summary = "公开-生成短链")
    @PostMapping("/create")
    public Result<CmsShortUrlResolveVO> create(@RequestBody CmsShortUrlCreateReq req) {
        try {
            return Result.ok(spiCmsShortUrlService.createShortUrl(req));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.warn("生成短链失败: {}", e.getMessage());
            return Result.error("生成短链失败");
        }
    }

    @Operation(summary = "公开-解析短链")
    @GetMapping("/resolve")
    public Result<CmsShortUrlResolveVO> resolve(@RequestParam(name = "key") String key) {
        try {
            return Result.ok(spiCmsShortUrlService.resolveByKey(key));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.warn("解析短链失败 key={}: {}", key, e.getMessage());
            return Result.error("短链无效");
        }
    }
}
