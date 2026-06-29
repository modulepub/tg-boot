package pub.module.dating.biz.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.constants.DatingWxSubscribeSceneEnum;
import pub.module.wx.api.dto.WxMaSubscribeTemplateDTO;
import pub.module.wx.api.service.ApiWxMaSubscribeTemplateService;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 公开-微信小程序订阅消息（模板 ID 由后台 wx_ma_subscribe_template 配置，免登录可拉取）。
 */
@Tag(name = "公开-微信订阅消息")
@RestController
@RequestMapping("/pub/dating/wxSubscribe")
public class PubDtWxSubscribeController {

    @Resource
    private ApiWxMaSubscribeTemplateService apiWxMaSubscribeTemplateService;

    @Data
    public static class TemplateItemVO implements Serializable {
        @Schema(description = "场景编码（模板编码）")
        private String sceneCode;
        @Schema(description = "场景说明")
        private String sceneDesc;
        @Schema(description = "微信模板 ID")
        private String templateId;
    }

    @Operation(summary = "公开-获取订阅消息模板 ID 列表（供小程序 requestSubscribeMessage）")
    @GetMapping("/templates")
    public Result<List<TemplateItemVO>> listTemplates() {
        List<String> templateCodes = Arrays.stream(DatingWxSubscribeSceneEnum.values())
                .map(DatingWxSubscribeSceneEnum::getTemplateCode)
                .toList();
        Map<String, WxMaSubscribeTemplateDTO> templateByCode = apiWxMaSubscribeTemplateService.listByCodes(templateCodes)
                .stream()
                .collect(Collectors.toMap(WxMaSubscribeTemplateDTO::getWxMaSubscribeTemplateCode, Function.identity(), (a, b) -> a));
        List<TemplateItemVO> list = Arrays.stream(DatingWxSubscribeSceneEnum.values())
                .map(scene -> {
                    TemplateItemVO vo = new TemplateItemVO();
                    vo.setSceneCode(scene.getTemplateCode());
                    vo.setSceneDesc(scene.getDesc());
                    WxMaSubscribeTemplateDTO template = templateByCode.get(scene.getTemplateCode());
                    if (template != null) {
                        vo.setTemplateId(template.getWxMaSubscribeTemplateId());
                    }
                    return vo;
                })
                .filter(vo -> vo.getTemplateId() != null && !vo.getTemplateId().isBlank())
                .toList();
        return Result.ok(list);
    }
}
