package pub.module.finance.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.finance.curd.entity.FcLoanBill;
import pub.module.finance.curd.service.IFcLoanBillService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 借贷分期账单
 * @author tg
 * @since 2025-10-02
 * @version V1.0
 */

@Tag(name="借贷分期账单")
@RestController
@RequestMapping("/finance/curd/fcLoanBill")
@Slf4j
public class FcLoanBillController{
	@Resource
	private IFcLoanBillService fcLoanBillService;
	
	@Operation(summary="借贷分期账单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcLoanBill>> queryPageList(FcLoanBill fcLoanBill,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcLoanBill> queryWrapper = WebQueryUtil.buildQuery(fcLoanBill);
		Page<FcLoanBill> page = new Page<>(pageNo, pageSize);
		IPage<FcLoanBill> pageList = fcLoanBillService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="借贷分期账单-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FcLoanBill fcLoanBill) {
		fcLoanBillService.save(fcLoanBill);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="借贷分期账单-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FcLoanBill fcLoanBill) {
		fcLoanBillService.updateById(fcLoanBill);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="借贷分期账单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		fcLoanBillService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="借贷分期账单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.fcLoanBillService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="借贷分期账单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FcLoanBill> queryById(@RequestParam(name="id") String id) {
		FcLoanBill fcLoanBill = fcLoanBillService.getById(id);
		return Result.ok(fcLoanBill);
	}

}
