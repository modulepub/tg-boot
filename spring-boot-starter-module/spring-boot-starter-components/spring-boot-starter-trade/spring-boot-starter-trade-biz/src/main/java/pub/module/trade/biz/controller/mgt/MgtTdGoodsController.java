package pub.module.trade.biz.controller.mgt;
import java.util.Collection;


import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

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
@Tag(name="管理端-td_goods")
@RestController
@RequestMapping("/mgt/trade/tdGoods")
@Slf4j
public class MgtTdGoodsController {
	@Resource
	private ITdGoodsService tdGoodsService;
	
	@Operation(summary="管理端-td_goods-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TdGoods>> queryPageList(TdGoods tdGoods,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdGoods> queryWrapper = WebQueryUtil.buildQuery(tdGoods);
		Page<TdGoods> page = new Page<>(pageNo, pageSize);
		IPage<TdGoods> pageList = tdGoodsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="管理端-td_goods-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TdGoods tdGoods) {
		tdGoodsService.save(tdGoods);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="管理端-td_goods-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TdGoods tdGoods) {
		tdGoodsService.updateById(tdGoods);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="管理端-td_goods-批量删除")
	@PostMapping(value = "/delete")
	public Result<String> deleteBatch(@RequestBody Collection<String> list) {
		this.tdGoodsService.removeByIds(list);
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="管理端-td_goods-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TdGoods> queryById(@RequestParam(name="id") String id) {
		TdGoods tdGoods = tdGoodsService.getById(id);
		return Result.ok(tdGoods);
	}

}
