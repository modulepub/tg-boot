package pub.module.dating.biz.controller.mgt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.vo.Result;
import pub.module.dating.crud.entity.DtMemberConfig;
import pub.module.dating.crud.service.DtMemberConfigService;

/**
 * 管理端-婚恋系统会员配置
 *
 * @author tg
 */
@Tag(name = "管理端-婚恋会员配置")
@RestController
@RequestMapping("/mgt/dating/memberConfig")
@Slf4j
public class MgtDtMemberConfigController {

    @Resource
    private DtMemberConfigService memberConfigService;

    @Operation(summary = "管理端-获取会员配置")
    @GetMapping(value = "/get")
    public Result<DtMemberConfig> get() {
        return Result.ok(memberConfigService.getOrInitConfig());
    }

    @Operation(summary = "管理端-保存会员配置")
    @PostMapping(value = "/save")
    public Result<DtMemberConfig> save(@RequestBody DtMemberConfig body) {
        StatusCodeEnum status = body == null ? StatusCodeEnum.NO : body.getCfgRegisterGiftFreevipStatusCode();
        return Result.ok(memberConfigService.saveRegisterGiftStatus(status));
    }
}
