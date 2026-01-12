package pub.module.trade.curd.controller;
import java.util.Arrays;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.trade.curd.entity.TdGoods;
import pub.module.trade.curd.service.ITdGoodsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 商品控制器
 * 提供商品相关的CRUD接口
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name="td_goods")
@RestController
@RequestMapping("/trade/curd/tdGoods")
@Slf4j
public class TdGoodsController{
	@Resource
	private ITdGoodsService tdGoodsService;
	
	@Operation(summary="td_goods-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TdGoods>> queryPageList(TdGoods tdGoods,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdGoods> queryWrapper = WebQueryUtil.buildQuery(tdGoods);
		Page<TdGoods> page = new Page<>(pageNo, pageSize);
		IPage<TdGoods> pageList = tdGoodsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="td_goods-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TdGoods tdGoods) {
		tdGoodsService.save(tdGoods);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="td_goods-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TdGoods tdGoods) {
		tdGoodsService.updateById(tdGoods);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="td_goods-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		tdGoodsService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="td_goods-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.tdGoodsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="td_goods-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TdGoods> queryById(@RequestParam(name="id") String id) {
		TdGoods tdGoods = tdGoodsService.getById(id);
		return Result.ok(tdGoods);
	}

}
