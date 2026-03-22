package pub.module.finance.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.finance.curd.entity.FcAccountLog;
import pub.module.finance.curd.service.IFcAccountLogService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 金融账户变动日志
 * @author tg
 * @since 2025-09-30
 * @version V1.0
 */

@Tag(name="金融账户变动日志")
@RestController
@RequestMapping("/finance/curd/fcAccountLog")
@Slf4j
public class FcAccountLogController{
	@Resource
	private IFcAccountLogService fcAccountLogService;
	
	@Operation(summary="金融账户变动日志-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcAccountLog>> queryPageList(FcAccountLog fcAccountLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcAccountLog> queryWrapper = WebQueryUtil.buildQuery(fcAccountLog);
		Page<FcAccountLog> page = new Page<>(pageNo, pageSize);
		IPage<FcAccountLog> pageList = fcAccountLogService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="金融账户变动日志-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FcAccountLog fcAccountLog) {
		fcAccountLogService.save(fcAccountLog);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="金融账户变动日志-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FcAccountLog fcAccountLog) {
		fcAccountLogService.updateById(fcAccountLog);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="金融账户变动日志-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		fcAccountLogService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="金融账户变动日志-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.fcAccountLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="金融账户变动日志-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FcAccountLog> queryById(@RequestParam(name="id") String id) {
		FcAccountLog fcAccountLog = fcAccountLogService.getById(id);
		return Result.ok(fcAccountLog);
	}

}
