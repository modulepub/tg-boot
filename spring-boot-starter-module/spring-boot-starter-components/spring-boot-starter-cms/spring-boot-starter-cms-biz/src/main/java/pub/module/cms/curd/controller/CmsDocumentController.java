package pub.module.cms.curd.controller;
import java.util.Arrays;

import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.cms.curd.entity.CmsDocument;
import pub.module.cms.curd.service.ICmsDocumentService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



/**
 * CMS文档
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */

@Tag(name="CMS文档")
@RestController
@RequestMapping("/cms/curd/cmsDocument")
@Slf4j
public class CmsDocumentController{
	@Resource
	private ICmsDocumentService cmsDocumentService;
	
	@Operation(summary="CMS文档-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsDocument>> queryPageList(CmsDocument cmsDocument,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<CmsDocument> queryWrapper = WebQueryUtil.buildQuery(cmsDocument);
		Page<CmsDocument> page = new Page<>(pageNo, pageSize);
		IPage<CmsDocument> pageList = cmsDocumentService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="CMS文档-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsDocument cmsDocument) {
		cmsDocumentService.save(cmsDocument);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="CMS文档-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsDocument cmsDocument) {
		cmsDocumentService.updateById(cmsDocument);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="CMS文档-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		cmsDocumentService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="CMS文档-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.cmsDocumentService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="CMS文档-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsDocument> queryById(@RequestParam(name="id") String id) {
		CmsDocument cmsDocument = cmsDocumentService.getById(id);
		return Result.ok(cmsDocument);
	}

}
