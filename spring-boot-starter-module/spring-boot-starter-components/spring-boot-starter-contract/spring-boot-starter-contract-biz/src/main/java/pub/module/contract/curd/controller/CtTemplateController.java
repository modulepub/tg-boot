package pub.module.contract.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.contract.curd.entity.CtTemplate;
import pub.module.contract.curd.service.ICtTemplateService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ct_template
 * @author tg
 * @since 2025-12-09
 * @version V1.0
 */

@Tag(name="ct_template")
@RestController
@RequestMapping("/contract/curd/ctTemplate")
@Slf4j
public class CtTemplateController{
	@Resource
	private ICtTemplateService ctTemplateService;
	
	@Operation(summary="ct_template-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CtTemplate>> queryPageList(CtTemplate ctTemplate,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<CtTemplate> queryWrapper = WebQueryUtil.buildQuery(ctTemplate);
		Page<CtTemplate> page = new Page<>(pageNo, pageSize);
		IPage<CtTemplate> pageList = ctTemplateService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="ct_template-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CtTemplate ctTemplate) {
		ctTemplateService.save(ctTemplate);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="ct_template-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CtTemplate ctTemplate) {
		ctTemplateService.updateById(ctTemplate);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="ct_template-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		ctTemplateService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="ct_template-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.ctTemplateService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="ct_template-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CtTemplate> queryById(@RequestParam(name="id") String id) {
		CtTemplate ctTemplate = ctTemplateService.getById(id);
		return Result.ok(ctTemplate);
	}

}
