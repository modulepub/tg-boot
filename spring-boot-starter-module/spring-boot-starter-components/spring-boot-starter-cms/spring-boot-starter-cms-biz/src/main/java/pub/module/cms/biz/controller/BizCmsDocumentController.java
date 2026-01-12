package pub.module.cms.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.cms.api.service.BizCmsService;
import pub.module.web.vo.Result;

@Tag(name = "CMS后台接口")
@RestController
@RequestMapping("/cms/biz/document")
@Slf4j
@AllArgsConstructor
public class BizCmsDocumentController {

    private BizCmsService bizCmsService;


    @Schema(description = "发布文档参数")
    @Data
    public static class PublishVO {
        @Schema(description = "文档编码")
        private String cdCode;
    }

    @Operation(summary = "发布文档")
    @PostMapping("/publishByCode")
    public Result<?> publishByCode(@RequestBody PublishVO publishVO) {
        bizCmsService.publishCmsDocument(publishVO.getCdCode());
        return Result.ok("发布成功！");
    }

    @Operation(summary = "撤销发布文档")
    @PostMapping("/cancelPublishByCode")
    public Result<?> cancelPublishByCode(@RequestBody PublishVO publishVO) {
        bizCmsService.cancelPublishCmsDocument(publishVO.getCdCode());
        return Result.ok("撤销发布成功！");
    }
}
