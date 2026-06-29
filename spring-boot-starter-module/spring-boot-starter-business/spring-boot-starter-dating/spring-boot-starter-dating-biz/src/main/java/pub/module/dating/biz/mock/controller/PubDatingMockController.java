package pub.module.dating.biz.mock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.dating.biz.mock.service.DatingMockSeedService;
import pub.module.dating.biz.mock.support.DatingMockSeedResult;

/**
 * 公开接口：一键导入 mock 素材（公司 / 红娘 / 客户）。
 * <p>GET {@code /pub/dating/mock/seed?confirm=yes}，登录可用返回手机号 + 验证码 666666。</p>
 */
@Tag(name = "公开-相亲 mock 数据")
@RestController
@RequestMapping("/pub/dating/mock")
@Slf4j
public class PubDatingMockController {

    @Resource
    private DatingMockSeedService datingMockSeedService;

    @Operation(summary = "导入 mock 数据（公司→红娘→客户→随机关联）")
    @GetMapping("/seed")
    public Result<DatingMockSeedResult> seed(
            @RequestParam(name = "confirm", defaultValue = "") String confirm,
            @RequestParam(name = "mockRoot", required = false) String mockRoot) {
        if (!"yes".equalsIgnoreCase(confirm)) {
            return Result.error("请传 confirm=yes 以确认执行 mock 导入");
        }
        try {
            DatingMockSeedResult result = datingMockSeedService.seed(mockRoot);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("mock seed failed", e);
            return Result.error("mock 导入失败: " + e.getMessage());
        }
    }
}
