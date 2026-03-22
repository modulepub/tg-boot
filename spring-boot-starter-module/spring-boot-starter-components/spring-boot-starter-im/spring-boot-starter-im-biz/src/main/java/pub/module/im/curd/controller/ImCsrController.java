package pub.module.im.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.im.curd.entity.ImCsr;
import pub.module.im.curd.service.IImCsrService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 客服坐席
 * @author tg
 * @since 2025-10-03
 * @version V1.0
 */

@Tag(name="客服坐席")
@RestController
@RequestMapping("/im/curd/imCsr")
@Slf4j
public class ImCsrController{
	@Resource
	private IImCsrService imCsrService;
	
	@Operation(summary="客服坐席-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ImCsr>> queryPageList(ImCsr imCsr,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<ImCsr> queryWrapper = WebQueryUtil.buildQuery(imCsr);
		Page<ImCsr> page = new Page<>(pageNo, pageSize);
		IPage<ImCsr> pageList = imCsrService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="客服坐席-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ImCsr imCsr) {
		imCsrService.save(imCsr);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="客服坐席-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ImCsr imCsr) {
		imCsrService.updateById(imCsr);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="客服坐席-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		imCsrService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="客服坐席-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.imCsrService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="客服坐席-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ImCsr> queryById(@RequestParam(name="id") String id) {
		ImCsr imCsr = imCsrService.getById(id);
		return Result.ok(imCsr);
	}

}
