package pub.module.finance.curd.controller;
import java.util.Arrays;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.finance.curd.entity.FcLoan;
import pub.module.finance.curd.service.IFcLoanService;

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
 * 信用借贷管理
 * @author tg
 * @since 2025-10-03
 * @version V1.0
 */

@Tag(name="信用借贷管理")
@RestController
@RequestMapping("/finance/curd/fcLoan")
@Slf4j
public class FcLoanController{
	@Resource
	private IFcLoanService fcLoanService;
	@Resource
	private IFcProductService fcProductService;
	
	@Operation(summary="信用借贷管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcLoan>> queryPageList(FcLoan fcLoan,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcLoan> queryWrapper = WebQueryUtil.buildQuery(fcLoan);
		Page<FcLoan> page = new Page<>(pageNo, pageSize);
		IPage<FcLoan> pageList = fcLoanService.page(page, queryWrapper);
		pageList.getRecords().forEach(item -> {
			item.setFcProduct(fcProductService.getByCode(item.getFcProductCode()));
		});
		return Result.ok(pageList);
	}
	
	@Operation(summary="信用借贷管理-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FcLoan fcLoan) {
		fcLoanService.save(fcLoan);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="信用借贷管理-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FcLoan fcLoan) {
		fcLoanService.updateById(fcLoan);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="信用借贷管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		fcLoanService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="信用借贷管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.fcLoanService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="信用借贷管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FcLoan> queryById(@RequestParam(name="id") String id) {
		FcLoan fcLoan = fcLoanService.getById(id);
		return Result.ok(fcLoan);
	}

}
