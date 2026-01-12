package pub.module.ba.curd.controller;
import java.util.Arrays;
import jakarta.annotation.Resource;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.ba.curd.entity.BaAppPage;
import pub.module.ba.curd.service.IBaAppPageService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 用户行为_锚点
 * @author tg
 * @since 2025-10-11
 * @version V1.0
 */

@Tag(name="用户行为_锚点")
@RestController
@RequestMapping("/ba/curd/baAppPage")
@Slf4j
public class BaAppPageController{
	@Resource
	private IBaAppPageService baAppPageService;

	@Operation(summary="用户行为_锚点-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<BaAppPage>> queryPageList(BaAppPage baAppPage,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<BaAppPage> queryWrapper = WebQueryUtil.buildQuery(baAppPage);
		Page<BaAppPage> page = new Page<>(pageNo, pageSize);
		IPage<BaAppPage> pageList = baAppPageService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="用户行为_锚点-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody BaAppPage baAppPage) {
		baAppPageService.save(baAppPage);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="用户行为_锚点-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody BaAppPage baAppPage) {
		baAppPageService.updateById(baAppPage);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="用户行为_锚点-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		baAppPageService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="用户行为_锚点-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.baAppPageService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="用户行为_锚点-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<BaAppPage> queryById(@RequestParam(name="id") String id) {
		BaAppPage baAppPage = baAppPageService.getById(id);
		return Result.ok(baAppPage);
	}

}
