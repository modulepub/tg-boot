package pub.module.finance.curd.controller;
import java.util.Arrays;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.service.IFcProductService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 产品管理
 * @author tg
 * @since 2025-10-11
 * @version V1.0
 */

@Tag(name="产品管理")
@RestController
@RequestMapping("/finance/curd/fcProduct")
@Slf4j
public class FcProductController{
	@Resource
	private IFcProductService fcProductService;
	
	@Operation(summary="产品管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcProduct>> queryPageList(FcProduct fcProduct,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcProduct> queryWrapper = WebQueryUtil.buildQuery(fcProduct);
		Page<FcProduct> page = new Page<>(pageNo, pageSize);
		IPage<FcProduct> pageList = fcProductService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="产品管理-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FcProduct fcProduct) {
		fcProductService.save(fcProduct);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="产品管理-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FcProduct fcProduct) {
		fcProductService.updateById(fcProduct);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="产品管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		fcProductService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="产品管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.fcProductService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="产品管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FcProduct> queryById(@RequestParam(name="id") String id) {
		FcProduct fcProduct = fcProductService.getById(id);
		return Result.ok(fcProduct);
	}

}
