package pub.module.trade.biz.controller.pub;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.curd.entity.TdOrder;
import pub.module.trade.curd.service.ITdOrderService;

import java.util.List;


/**
 * 订单
 * @author tg
 * @since 2025-06-10
 * @version V1.0
 */
@Tag(name ="公开-订单")
@RestController
@RequestMapping("/pub/trade/tradeOrder")
@Slf4j
public class PubTradeOrderController {
	@Resource
	private ITdOrderService tradeOrderService;

	@Operation(summary = "公开-订单-通过订单编码查询")
	@GetMapping(value = "/queryByCode")
	public Result<TdOrder> queryByCode(@RequestParam(name="tdOdCode") String tdOdCode) {
		TdOrder tdOrder = tradeOrderService.getOne(new QueryWrapper<TdOrder>().lambda().eq(TdOrder::getTdOdCode,tdOdCode),false);
		return Result.ok(tdOrder);
	}

}
