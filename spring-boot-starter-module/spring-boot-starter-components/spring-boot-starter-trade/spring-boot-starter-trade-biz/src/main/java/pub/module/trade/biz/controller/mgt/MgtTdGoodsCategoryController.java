package pub.module.trade.biz.controller.mgt;
import java.util.Collection;


import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

import pub.module.trade.curd.entity.TdGoodsCategory;
import pub.module.trade.curd.service.ITdGoodsCategoryService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 商品分类控制器
 * 提供商品分类相关的CRUD接口
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name="管理端-td_goods_category")
@RestController
@RequestMapping("/mgt/trade/tdGoodsCategory")
@Slf4j
public class MgtTdGoodsCategoryController {
	@Resource
	private ITdGoodsCategoryService tdGoodsCategoryService;
	
	@Operation(summary="管理端-td_goods_category-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TdGoodsCategory>> queryPageList(TdGoodsCategory tdGoodsCategory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdGoodsCategory> queryWrapper = WebQueryUtil.buildQuery(tdGoodsCategory);
		Page<TdGoodsCategory> page = new Page<>(pageNo, pageSize);
		IPage<TdGoodsCategory> pageList = tdGoodsCategoryService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="管理端-td_goods_category-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TdGoodsCategory tdGoodsCategory) {
		tdGoodsCategoryService.save(tdGoodsCategory);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="管理端-td_goods_category-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TdGoodsCategory tdGoodsCategory) {
		tdGoodsCategoryService.updateById(tdGoodsCategory);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="管理端-td_goods_category-批量删除")
	@PostMapping(value = "/delete")
	public Result<String> deleteBatch(@RequestBody Collection<String> list) {
		this.tdGoodsCategoryService.removeByIds(list);
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="管理端-td_goods_category-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TdGoodsCategory> queryById(@RequestParam(name="id") String id) {
		TdGoodsCategory tdGoodsCategory = tdGoodsCategoryService.getById(id);
		return Result.ok(tdGoodsCategory);
	}

}
