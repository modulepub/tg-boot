package pub.module.ba.curd.controller;
import java.util.Arrays;


import pub.module.ba.curd.service.IBaAppService;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.ba.curd.entity.BaApp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 行为分析APP
 * @author tg
 * @since 2025-10-10
 * @version V1.0
 */

@Tag(name="行为分析APP")
@RestController
@RequestMapping("/ba/curd/baApp")
@Slf4j
public class BaAppController{
	@Resource
	private IBaAppService baAppService;

	@Operation(summary="行为分析APP-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BaApp>> queryPageList(BaApp baApp,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<BaApp> queryWrapper = WebQueryUtil.buildQuery(baApp);
		Page<BaApp> page = new Page<>(pageNo, pageSize);
		IPage<BaApp> pageList = baAppService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	@Operation(summary="行为分析APP-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BaApp baApp) {
		baAppService.save(baApp);
		return Result.ok("添加成功！");
	}

	@Operation(summary="行为分析APP-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BaApp baApp) {
		baAppService.updateById(baApp);
		return Result.ok("编辑成功!");
	}

	@Operation(summary="行为分析APP-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		baAppService.removeById(id);
		return Result.ok("删除成功!");
	}

	@Operation(summary="行为分析APP-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.baAppService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	@Operation(summary="行为分析APP-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BaApp> queryById(@RequestParam(name="id") String id) {
		BaApp baApp = baAppService.getById(id);
		return Result.ok(baApp);
	}

}
