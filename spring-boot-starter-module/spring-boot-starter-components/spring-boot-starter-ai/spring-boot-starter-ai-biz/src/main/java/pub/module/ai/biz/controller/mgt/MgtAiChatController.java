package pub.module.ai.biz.controller.mgt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.ai.api.dto.AiChatRequestDTO;
import pub.module.ai.api.dto.AiChatResponseDTO;
import pub.module.ai.api.service.ApiAiChatService;
import pub.module.common.model.vo.Result;

@Tag(name = "管理端-AI 对话")
@RestController
@RequestMapping("/mgt/ai")
public class MgtAiChatController {

    @Resource
    private ApiAiChatService apiAiChatService;

    @Operation(summary = "管理端-AI 对话调用示例")
    @PostMapping("/chat")
    public Result<AiChatResponseDTO> chat(@RequestBody AiChatRequestDTO request) {
        AiChatResponseDTO response = apiAiChatService.chat(request);
        return Result.ok(response);
    }
}
