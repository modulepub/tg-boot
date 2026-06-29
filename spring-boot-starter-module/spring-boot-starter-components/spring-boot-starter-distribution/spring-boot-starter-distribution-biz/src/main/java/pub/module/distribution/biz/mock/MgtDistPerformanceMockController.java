package pub.module.distribution.biz.mock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;

/**
 * 管理端：业绩（绩效）测试数据生成与清除。
 */
@Tag(name = "管理端-业绩测试数据")
@RestController
@RequestMapping("/mgt/distribution/mock")
@Slf4j
public class MgtDistPerformanceMockController {

    @Resource
    private DistPerformanceMockSeedService distPerformanceMockSeedService;
    @Resource
    private DistPerformanceMockClearService distPerformanceMockClearService;

    @Operation(summary = "生成业绩测试数据")
    @PostMapping("/seed")
    public Result<DistPerformanceMockSeedResult> seed(
            @RequestParam(name = "promoterUserCode") String promoterUserCode,
            @RequestParam(name = "downlineCount", defaultValue = "5") Integer downlineCount) {
        try {
            DistPerformanceMockSeedResult result =
                    distPerformanceMockSeedService.seed(promoterUserCode, downlineCount);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("dist performance mock seed failed", e);
            return Result.error("业绩测试数据生成失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清除业绩测试数据")
    @PostMapping("/clear")
    public Result<DistPerformanceMockClearResult> clear(
            @RequestParam(name = "confirm", defaultValue = "") String confirm) {
        if (!"yes".equalsIgnoreCase(confirm)) {
            return Result.error("请传 confirm=yes 以确认清除全部业绩测试数据");
        }
        try {
            return Result.ok(distPerformanceMockClearService.clear());
        } catch (Exception e) {
            log.error("dist performance mock clear failed", e);
            return Result.error("业绩测试数据清除失败: " + e.getMessage());
        }
    }
}
