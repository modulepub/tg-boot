package pub.module.finance.curd.controller;
import java.util.Arrays;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.finance.curd.entity.FcLoanBillSettleLog;
import pub.module.finance.curd.service.IFcLoanBillSettleLogService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 借贷分期账单还款记录
 * @author tg
 * @since 2025-10-02
 * @version V1.0
 */

@Tag(name="借贷分期账单还款记录")
@RestController
@RequestMapping("/finance/curd/fcLoanBillSettleLog")
@Slf4j
public class FcLoanBillSettleLogController{
	@Resource
	private IFcLoanBillSettleLogService fcLoanBillSettleLogService;
	
	@Operation(summary="借贷分期账单还款记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcLoanBillSettleLog>> queryPageList(FcLoanBillSettleLog fcLoanBillSettleLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcLoanBillSettleLog> queryWrapper = WebQueryUtil.buildQuery(fcLoanBillSettleLog);
		Page<FcLoanBillSettleLog> page = new Page<>(pageNo, pageSize);
		IPage<FcLoanBillSettleLog> pageList = fcLoanBillSettleLogService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="借贷分期账单还款记录-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FcLoanBillSettleLog fcLoanBillSettleLog) {
		fcLoanBillSettleLogService.save(fcLoanBillSettleLog);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="借贷分期账单还款记录-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FcLoanBillSettleLog fcLoanBillSettleLog) {
		fcLoanBillSettleLogService.updateById(fcLoanBillSettleLog);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="借贷分期账单还款记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		fcLoanBillSettleLogService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="借贷分期账单还款记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.fcLoanBillSettleLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="借贷分期账单还款记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FcLoanBillSettleLog> queryById(@RequestParam(name="id") String id) {
		FcLoanBillSettleLog fcLoanBillSettleLog = fcLoanBillSettleLogService.getById(id);
		return Result.ok(fcLoanBillSettleLog);
	}

}
