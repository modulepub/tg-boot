package pub.module.ai.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import pub.module.ai.api.dto.AiChatMessageDTO;
import pub.module.ai.crud.entity.AiChatMessage;
import pub.module.ai.crud.entity.AiChatSession;
import pub.module.ai.crud.service.IAiChatMessageService;
import pub.module.ai.crud.service.IAiChatSessionService;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "管理端-ai_chat_session")
@RestController
@RequestMapping("/mgt/ai/aiChatSession")
public class MgtAiChatSessionController {

    @Resource
    private IAiChatSessionService aiChatSessionService;
    @Resource
    private IAiChatMessageService aiChatMessageService;

    @Operation(summary = "管理端-ai_chat_session-分页列表查询")
    @GetMapping("/list")
    public Result<IPage<AiChatSession>> queryPageList(AiChatSession entity,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<AiChatSession> queryWrapper = WebQueryUtil.buildQuery(entity);
        queryWrapper.orderByDesc("create_time");
        Page<AiChatSession> page = new Page<>(pageNo, pageSize);
        return Result.ok(aiChatSessionService.page(page, queryWrapper));
    }

    @Operation(summary = "管理端-ai_chat_session-批量删除")
    @PostMapping("/delete")
    public Result<String> deleteBatch(@RequestBody Collection<String> list) {
        aiChatSessionService.removeByBizCodes(list);
        return Result.ok("批量删除成功!");
    }

    @Operation(summary = "管理端-ai_chat_session-按业务编码查询")
    @GetMapping("/queryById")
    public Result<AiChatSession> queryById(@RequestParam(name = "id") String id) {
        return Result.ok(aiChatSessionService.getByCode(id));
    }

    @Operation(summary = "管理端-ai_chat_session-查询会话全部消息")
    @GetMapping("/messages")
    public Result<List<AiChatMessageDTO>> messages(@RequestParam(name = "aiChatSessionCode") String aiChatSessionCode) {
        List<AiChatMessage> messages = aiChatMessageService.listBySessionCode(aiChatSessionCode);
        List<AiChatMessageDTO> dtos = messages.stream().map(m -> {
            AiChatMessageDTO dto = new AiChatMessageDTO();
            dto.setAiChatMessageRoleCode(m.getAiChatMessageRoleCode());
            dto.setAiChatMessageContent(m.getAiChatMessageContent());
            return dto;
        }).collect(Collectors.toList());
        return Result.ok(dtos);
    }
}
