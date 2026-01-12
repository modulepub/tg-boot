package pub.module.finance.curd.controller;
import java.util.Arrays;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.finance.curd.entity.FcCreditExtension;
import pub.module.finance.curd.service.IFcCreditExtensionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import pub.module.finance.curd.service.IFcProductService;

/**
 * 授信表
 * @author tg
 * @since 2025-11-02
 * @version V1.0
 */

@Tag(name="授信表")
@RestController
@RequestMapping("/finance/curd/fcCreditExtension")
@Slf4j
public class FcCreditExtensionController{
	@Resource
	private IFcCreditExtensionService fcCreditExtensionService;
	@Resource
	private IFcProductService fcProductService;

	@Operation(summary="授信表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcCreditExtension>> queryPageList(FcCreditExtension fcCreditExtension,
														  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcCreditExtension> queryWrapper = WebQueryUtil.buildQuery(fcCreditExtension);
		Page<FcCreditExtension> page = new Page<>(pageNo, pageSize);
		IPage<FcCreditExtension> pageList = fcCreditExtensionService.page(page, queryWrapper);
		pageList.getRecords().forEach(item -> {
			item.setFcProduct(fcProductService.getByCode(item.getFcProductCode()));
		});
		return Result.ok(pageList);
	}
	
	@Operation(summary="授信表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FcCreditExtension fcCreditExtension) {
		fcCreditExtensionService.save(fcCreditExtension);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="授信表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FcCreditExtension fcCreditExtension) {
		fcCreditExtensionService.updateById(fcCreditExtension);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="授信表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		fcCreditExtensionService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="授信表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.fcCreditExtensionService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="授信表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FcCreditExtension> queryById(@RequestParam(name="id") String id) {
		FcCreditExtension fcCreditExtension = fcCreditExtensionService.getById(id);
		return Result.ok(fcCreditExtension);
	}

}
