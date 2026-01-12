package pub.module.finance.curd.controller;
import java.util.Arrays;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.finance.curd.entity.FcAccount;
import pub.module.finance.curd.service.IFcAccountService;

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
 * 金融账户管理
 * @author tg
 * @since 2025-10-09
 * @version V1.0
 */

@Tag(name="金融账户管理")
@RestController
@RequestMapping("/finance/curd/fcAccount")
@Slf4j
public class FcAccountController{
	@Resource
	private IFcAccountService fcAccountService;
	@Resource
	private IFcProductService fcProductService;
	
	@Operation(summary="金融账户管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcAccount>> queryPageList(FcAccount fcAccount,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcAccount> queryWrapper = WebQueryUtil.buildQuery(fcAccount);
		Page<FcAccount> page = new Page<>(pageNo, pageSize);
		IPage<FcAccount> pageList = fcAccountService.page(page, queryWrapper);
		pageList.getRecords().forEach(item -> {
			item.setFcProduct(fcProductService.getByCode(item.getFcProductCode()));
		});
		return Result.ok(pageList);
	}
	
	@Operation(summary="金融账户管理-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FcAccount fcAccount) {
		fcAccountService.save(fcAccount);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="金融账户管理-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FcAccount fcAccount) {
		fcAccountService.updateById(fcAccount);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="金融账户管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		fcAccountService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="金融账户管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.fcAccountService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="金融账户管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FcAccount> queryById(@RequestParam(name="id") String id) {
		FcAccount fcAccount = fcAccountService.getById(id);
		return Result.ok(fcAccount);
	}

}
