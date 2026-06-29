package pub.module.dating.biz.mock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.dating.biz.mock.service.DatingMockClearService;
import pub.module.dating.biz.mock.service.DatingMockSeedService;
import pub.module.dating.biz.mock.support.DatingMockClearResult;
import pub.module.dating.biz.mock.support.DatingMockSeedResult;

/**
 * 管理端：测试数据生成与清除。
 */
@Tag(name = "管理端-相亲测试数据")
@RestController
@RequestMapping("/mgt/dating/mock")
@Slf4j
public class MgtDatingMockController {

    @Resource
    private DatingMockSeedService datingMockSeedService;
    @Resource
    private DatingMockClearService datingMockClearService;

    @Operation(summary = "一键生成测试数据")
    @PostMapping("/seed")
    public Result<DatingMockSeedResult> seed(
            @RequestParam(name = "mockRoot", required = false) String mockRoot) {
        try {
            DatingMockSeedResult result = datingMockSeedService.seed(mockRoot);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("mgt mock seed failed", e);
            return Result.error("测试数据生成失败: " + e.getMessage());
        }
    }

    @Operation(summary = "一键删除测试数据")
    @PostMapping("/clear")
    public Result<DatingMockClearResult> clear(
            @RequestParam(name = "confirm", defaultValue = "") String confirm) {
        if (!"yes".equalsIgnoreCase(confirm)) {
            return Result.error("请传 confirm=yes 以确认删除全部测试数据");
        }
        try {
            DatingMockClearResult result = datingMockClearService.clear();
            return Result.ok(result);
        } catch (Exception e) {
            log.error("mgt mock clear failed", e);
            return Result.error("测试数据清除失败: " + e.getMessage());
        }
    }
}
