package pub.module.sms.biz.controller.mgt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.sms.api.dto.SendSmsDTO;
import pub.module.sms.api.service.ApiSmsSendService;

@Tag(name = "管理端-短信发送")
@RestController
@RequestMapping("/mgt/sms")
public class MgtSmsSendController {

    @Resource
    private ApiSmsSendService apiSmsSendService;

    @Operation(summary = "管理端-发送短信（记录日志）")
    @PostMapping("/send")
    public Result<String> send(@RequestBody SendSmsDTO dto) {
        String result = apiSmsSendService.sendSms(dto);
        return Result.ok(result != null ? result : "发送完成");
    }
}
