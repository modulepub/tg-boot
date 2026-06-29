package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.service.ApiDtMatchmakerOrderService;
import pub.module.dating.api.service.dto.MkServiceOrderItemDTO;
import pub.module.dating.api.service.dto.MkServiceOrderStatsDTO;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

/**
 * 用户端-红娘工作台服务订单
 */
@Tag(name = "用户端-红娘工作台服务订单")
@RestController
@RequestMapping("/cus/dating/mkServiceOrder")
@Slf4j
public class CusDtMatchmakerOrderController {

    @Resource
    private ApiDtMatchmakerOrderService apiDtMatchmakerOrderService;

    @Operation(summary = "红娘工作台-服务订单明细分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<MkServiceOrderItemDTO>> list(
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        IPage<MkServiceOrderItemDTO> pageList = apiDtMatchmakerOrderService.listServiceOrders(
            userDTO.getUserCode(), pageNo, pageSize);
        return Result.ok(pageList);
    }

    @Operation(summary = "红娘工作台-服务订单汇总统计")
    @GetMapping(value = "/stats")
    public Result<MkServiceOrderStatsDTO> stats() {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        MkServiceOrderStatsDTO stats = apiDtMatchmakerOrderService.getServiceOrderStats(userDTO.getUserCode());
        return Result.ok(stats);
    }
}
