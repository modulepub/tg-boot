package pub.module.trade.biz.controller.mgt;
import java.util.Arrays;


import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.trade.curd.entity.TdOrderGoods;
import pub.module.trade.curd.service.ITdOrderGoodsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 订单商品控制器
 * 提供订单商品相关的CRUD接口
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name="管理端-td_order_goods")
@RestController
@RequestMapping("/mgt/trade/tdOrderGoods")
@Slf4j
public class MgtTdOrderGoodsController {
	@Resource
	private ITdOrderGoodsService tdOrderGoodsService;
	
	@Operation(summary="管理端-td_order_goods-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TdOrderGoods>> queryPageList(TdOrderGoods tdOrderGoods,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdOrderGoods> queryWrapper = WebQueryUtil.buildQuery(tdOrderGoods);
		Page<TdOrderGoods> page = new Page<>(pageNo, pageSize);
		IPage<TdOrderGoods> pageList = tdOrderGoodsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="管理端-td_order_goods-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TdOrderGoods tdOrderGoods) {
		tdOrderGoodsService.save(tdOrderGoods);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="管理端-td_order_goods-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TdOrderGoods tdOrderGoods) {
		tdOrderGoodsService.updateById(tdOrderGoods);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="管理端-td_order_goods-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		tdOrderGoodsService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="管理端-td_order_goods-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.tdOrderGoodsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="管理端-td_order_goods-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TdOrderGoods> queryById(@RequestParam(name="id") String id) {
		TdOrderGoods tdOrderGoods = tdOrderGoodsService.getById(id);
		return Result.ok(tdOrderGoods);
	}

}
