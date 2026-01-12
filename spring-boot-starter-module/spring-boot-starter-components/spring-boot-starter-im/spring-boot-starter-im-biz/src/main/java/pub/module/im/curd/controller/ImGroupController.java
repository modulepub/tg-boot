package pub.module.im.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.im.curd.entity.ImGroup;
import pub.module.im.curd.service.IImGroupService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 即时通讯群组
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */

@Tag(name="即时通讯群组")
@RestController
@RequestMapping("/im/curd/imGroup")
@Slf4j
public class ImGroupController{
	@Resource
	private IImGroupService imGroupService;
	
	@Operation(summary="即时通讯群组-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ImGroup>> queryPageList(ImGroup imGroup,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<ImGroup> queryWrapper = WebQueryUtil.buildQuery(imGroup);
		Page<ImGroup> page = new Page<>(pageNo, pageSize);
		IPage<ImGroup> pageList = imGroupService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="即时通讯群组-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ImGroup imGroup) {
		imGroupService.save(imGroup);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="即时通讯群组-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ImGroup imGroup) {
		imGroupService.updateById(imGroup);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="即时通讯群组-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		imGroupService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="即时通讯群组-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.imGroupService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="即时通讯群组-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ImGroup> queryById(@RequestParam(name="id") String id) {
		ImGroup imGroup = imGroupService.getById(id);
		return Result.ok(imGroup);
	}

}
