package pub.module.trade.biz.controller.mgt;
import java.util.Collection;


import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.trade.curd.entity.TdOrder;
import pub.module.trade.curd.service.ITdOrderService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 订单控制器
 * 提供订单相关的CRUD接口
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name="管理端-td_order")
@RestController
@RequestMapping("/mgt/trade/tdOrder")
@Slf4j
public class MgtTdOrderController {
	@Resource
	private ITdOrderService tdOrderService;
	
	@Operation(summary="管理端-td_order-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TdOrder>> queryPageList(TdOrder tdOrder,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdOrder> queryWrapper = WebQueryUtil.buildQuery(tdOrder);
		Page<TdOrder> page = new Page<>(pageNo, pageSize);
		IPage<TdOrder> pageList = tdOrderService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="管理端-td_order-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TdOrder tdOrder) {
		tdOrderService.save(tdOrder);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="管理端-td_order-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TdOrder tdOrder) {
		tdOrderService.updateById(tdOrder);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="管理端-td_order-批量删除")
	@PostMapping(value = "/delete")
	public Result<String> deleteBatch(@RequestBody Collection<String> list) {
		this.tdOrderService.removeByIds(list);
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="管理端-td_order-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TdOrder> queryById(@RequestParam(name="id") String id) {
		TdOrder tdOrder = tdOrderService.getById(id);
		return Result.ok(tdOrder);
	}

}
