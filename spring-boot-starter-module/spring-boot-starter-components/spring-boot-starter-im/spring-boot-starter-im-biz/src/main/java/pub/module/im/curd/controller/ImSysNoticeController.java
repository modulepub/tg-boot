package pub.module.im.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.im.curd.entity.ImSysNotice;
import pub.module.im.curd.service.IImSysNoticeService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 系统通知
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */

@Tag(name="系统通知")
@RestController
@RequestMapping("/im/curd/imSysNotice")
@Slf4j
public class ImSysNoticeController{
	@Resource
	private IImSysNoticeService imSysNoticeService;
	
	@Operation(summary="系统通知-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ImSysNotice>> queryPageList(ImSysNotice imSysNotice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<ImSysNotice> queryWrapper = WebQueryUtil.buildQuery(imSysNotice);
		Page<ImSysNotice> page = new Page<>(pageNo, pageSize);
		IPage<ImSysNotice> pageList = imSysNoticeService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="系统通知-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ImSysNotice imSysNotice) {
		imSysNoticeService.save(imSysNotice);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="系统通知-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ImSysNotice imSysNotice) {
		imSysNoticeService.updateById(imSysNotice);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="系统通知-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		imSysNoticeService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="系统通知-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.imSysNoticeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="系统通知-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ImSysNotice> queryById(@RequestParam(name="id") String id) {
		ImSysNotice imSysNotice = imSysNoticeService.getById(id);
		return Result.ok(imSysNotice);
	}

}
