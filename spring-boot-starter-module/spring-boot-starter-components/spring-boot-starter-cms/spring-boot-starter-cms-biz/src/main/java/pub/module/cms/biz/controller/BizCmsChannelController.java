package pub.module.cms.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.cms.api.service.BizCmsService;
import pub.module.web.vo.Result;

@Tag(name = "CMS后台接口")
@RestController
@RequestMapping("/cms/biz/channel")
@Slf4j
@AllArgsConstructor
public class BizCmsChannelController {

    private BizCmsService bizCmsService;


    @Schema(description = "发布栏目参数")
    @Data
    public static class PublishVO {
        @Schema(description = "栏目编码")
        private String ccCode;
    }

    @Operation(summary = "发布栏目")
    @PostMapping("/publishByCode")
    public Result<?> publishByCode(@RequestBody PublishVO publishVO) {
        bizCmsService.publishCmsChannel(publishVO.getCcCode());
        return Result.ok("发布成功！");
    }

    @Operation(summary = "撤销发布栏目")
    @PostMapping("/cancelPublishByCode")
    public Result<?> cancelPublishByCode(@RequestBody PublishVO publishVO) {
        bizCmsService.cancelPublishCmsChannel(publishVO.getCcCode());
        return Result.ok("撤销发布成功！");
    }
}
