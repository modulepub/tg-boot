package pub.module.trade.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.trade.biz.service.BizTradeOrderService;
import pub.module.trade.curd.service.ITdOrderService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import pub.module.system.api.util.UserUtil;
import pub.module.trade.curd.entity.TdOrder;
import pub.module.trade.curd.entity.TdOrderGoods;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;


/**
 * 订单
 * @author tg
 * @since 2025-06-10
 * @version V1.0
 */
@Tag(name ="用户端-订单")
@RestController
@RequestMapping("/cus/trade/tradeOrder")
@Slf4j
public class CusTradeOrderController {
	@Resource
	private ITdOrderService tradeOrderService;
    @Resource
    BizTradeOrderService bizTradeOrderService;

	@Operation(summary = "用户端-订单-分页列表查询")
	@GetMapping(value = "/listByUser")
	public Result<IPage<TdOrder>> queryPageList(TdOrder tdOrder,
                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdOrder> queryWrapper = WebQueryUtil.buildQuery(tdOrder);
		queryWrapper.lambda().eq(TdOrder::getTdOdSysUserCode, UserUtil.getCurrentSysUser().getUserCode());
		Page<TdOrder> page = new Page<>(pageNo, pageSize);
		IPage<TdOrder> pageList = tradeOrderService.page(page, queryWrapper);
		return Result.ok(pageList);
	}


	@Operation(summary = "用户端-订单-创建订单")
	@PostMapping(value = "/create")
	public Result<TdOrder> add(@RequestBody List<BizTradeOrderService.OrderGoodsDTO> tdOrderGoodsList) {
        UserDTO sysUser = UserUtil.getCurrentSysUser();
		TdOrder tdOrder = bizTradeOrderService.createOrder(tdOrderGoodsList,sysUser.getUserCode()	,sysUser.getUserRealName(),sysUser.getUserPhone());
		return Result.ok(tdOrder);
	}
	

	@Operation(summary = "用户端-订单-通过订单编码查询")
	@GetMapping(value = "/queryByCode")
	public Result<TdOrder> queryByCode(@RequestParam(name="tdOdCode") String tdOdCode) {
		TdOrder tdOrder = tradeOrderService.getOne(new QueryWrapper<TdOrder>().lambda().eq(TdOrder::getTdOdCode,tdOdCode),false);
		return Result.ok(tdOrder);
	}

}
