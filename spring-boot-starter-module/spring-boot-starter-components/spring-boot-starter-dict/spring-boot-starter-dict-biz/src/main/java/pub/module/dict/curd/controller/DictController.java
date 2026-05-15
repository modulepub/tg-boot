package pub.module.dict.curd.controller;
import java.util.Arrays;


import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;

import pub.module.dict.curd.entity.Dict;
import pub.module.dict.curd.service.DictService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * sys_dict
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */

@Tag(name="sys_dict")
@RestController
@RequestMapping("/dict/curd/sysDict")
@Slf4j
public class DictController {
	@Resource
	private DictService sysDictService;
	
	@Operation(summary="sys_dict-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Dict>> queryPageList(Dict dict,
											 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<Dict> queryWrapper = WebQueryUtil.buildQuery(dict);
		Page<Dict> page = new Page<>(pageNo, pageSize);
		IPage<Dict> pageList = sysDictService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="sys_dict-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Dict dict) {
		sysDictService.save(dict);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="sys_dict-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Dict dict) {
		sysDictService.updateById(dict);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="sys_dict-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		sysDictService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="sys_dict-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.sysDictService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="sys_dict-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Dict> queryById(@RequestParam(name="id") String id) {
		Dict dict = sysDictService.getById(id);
		return Result.ok(dict);
	}

}
