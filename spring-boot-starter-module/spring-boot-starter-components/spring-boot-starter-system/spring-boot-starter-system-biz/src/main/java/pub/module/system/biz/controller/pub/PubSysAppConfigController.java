package pub.module.system.biz.controller.pub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.system.api.service.dto.SysAppConfigPubDTO;
import pub.module.system.crud.entity.SysAppConfig;
import pub.module.system.crud.service.SysAppConfigService;

import java.util.Collections;
import java.util.Map;

/**
 * 公开-APP配置
 */
@Tag(name = "公开-APP配置")
@RestController
@RequestMapping("/pub/system/sysAppConfig")
@Slf4j
public class PubSysAppConfigController {

    @Resource
    private SysAppConfigService sysAppConfigService;

    @Operation(summary = "公开-按 key 查询 APP 配置")
    @GetMapping("/getByKey")
    public Result<SysAppConfigPubDTO> getByKey(@RequestParam(name = "appConfigKey") String appConfigKey) {
        if (StrUtil.isBlank(appConfigKey)) {
            throw new IllegalArgumentException("配置 key 不能为空");
        }
        SysAppConfig config = sysAppConfigService.getByAppConfigKey(appConfigKey.trim());
        if (config == null) {
            return Result.ok(null);
        }
        SysAppConfigPubDTO dto = new SysAppConfigPubDTO();
        dto.setAppConfigKey(config.getAppConfigKey());
        dto.setAppConfigValue(parseAppConfigValue(config.getAppConfigValue()));
        return Result.ok(dto);
    }

    private Map<String, Object> parseAppConfigValue(String appConfigValue) {
        if (StrUtil.isBlank(appConfigValue)) {
            return Collections.emptyMap();
        }
        JSONObject jsonObject = JSONUtil.parseObj(appConfigValue);
        return jsonObject == null ? Collections.emptyMap() : jsonObject;
    }
}
