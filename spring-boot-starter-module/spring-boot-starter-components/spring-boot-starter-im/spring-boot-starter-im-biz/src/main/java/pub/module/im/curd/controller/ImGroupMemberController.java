package pub.module.im.curd.controller;
import java.util.Arrays;


import pub.module.web.vo.Result;
import pub.module.web.util.WebQueryUtil;

import pub.module.im.curd.entity.ImGroupMember;
import pub.module.im.curd.service.IImGroupMemberService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 群组成员
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */

@Tag(name="群组成员")
@RestController
@RequestMapping("/im/curd/imGroupMember")
@Slf4j
public class ImGroupMemberController{
	@Resource
	private IImGroupMemberService imGroupMemberService;
	
	@Operation(summary="群组成员-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ImGroupMember>> queryPageList(ImGroupMember imGroupMember,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<ImGroupMember> queryWrapper = WebQueryUtil.buildQuery(imGroupMember);
		Page<ImGroupMember> page = new Page<>(pageNo, pageSize);
		IPage<ImGroupMember> pageList = imGroupMemberService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="群组成员-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ImGroupMember imGroupMember) {
		imGroupMemberService.save(imGroupMember);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="群组成员-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ImGroupMember imGroupMember) {
		imGroupMemberService.updateById(imGroupMember);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="群组成员-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		imGroupMemberService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="群组成员-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.imGroupMemberService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="群组成员-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ImGroupMember> queryById(@RequestParam(name="id") String id) {
		ImGroupMember imGroupMember = imGroupMemberService.getById(id);
		return Result.ok(imGroupMember);
	}

}
