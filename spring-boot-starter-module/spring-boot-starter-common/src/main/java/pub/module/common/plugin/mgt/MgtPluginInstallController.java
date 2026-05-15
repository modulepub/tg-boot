package pub.module.common.plugin.mgt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.common.plugin.model.PluginInstallRecord;
import pub.module.common.plugin.registry.PluginInstallRegistry;

import java.util.List;

@Tag(name = "管理端-插件安装状态")
@RestController
@RequestMapping("/mgt/plugin")
public class MgtPluginInstallController {

    @Operation(summary = "外部插件安装与加载情况（内存）")
    @GetMapping("/install/list")
    public Result<List<PluginInstallRecord>> list() {
        return Result.ok(PluginInstallRegistry.snapshot());
    }
}
