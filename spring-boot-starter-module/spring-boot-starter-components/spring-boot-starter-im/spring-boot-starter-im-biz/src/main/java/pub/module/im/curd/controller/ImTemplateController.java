package pub.module.im.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.im.curd.entity.ImTemplate;
import pub.module.im.curd.service.IImTemplateService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 消息模板
 * @author tg
 * @since 2025-10-27
 * @version V1.0
 */

@Tag(name="消息模板")
@RestController
@RequestMapping("/im/curd/imTemplate")
@Slf4j
public class ImTemplateController{
	@Resource
	private IImTemplateService imTemplateService;
	
	@Operation(summary="消息模板-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ImTemplate>> queryPageList(ImTemplate imTemplate,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<ImTemplate> queryWrapper = WebQueryUtil.buildQuery(imTemplate);
		Page<ImTemplate> page = new Page<>(pageNo, pageSize);
		IPage<ImTemplate> pageList = imTemplateService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="消息模板-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ImTemplate imTemplate) {
		imTemplateService.save(imTemplate);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="消息模板-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ImTemplate imTemplate) {
		imTemplateService.updateById(imTemplate);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="消息模板-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		imTemplateService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="消息模板-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.imTemplateService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="消息模板-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ImTemplate> queryById(@RequestParam(name="id") String id) {
		ImTemplate imTemplate = imTemplateService.getById(id);
		return Result.ok(imTemplate);
	}

}
