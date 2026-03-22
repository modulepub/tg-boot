package pub.module.dict.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dict.curd.entity.Dict;
import pub.module.dict.curd.service.DictService;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import java.util.Collection;

@Tag(name="字典管理")
@RestController
@RequestMapping("/mgt/sysDict")
@Slf4j
public class MgtDictController {
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
	@PostMapping(value = "/edit")
	public Result<String> edit(@RequestBody Dict dict) {
		sysDictService.updateById(dict);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="sys_dict-通过id删除")
	@PostMapping(value = "/delete")
	public Result<String> delete(@RequestBody Collection<String> ids) {
		sysDictService.removeByIds(ids);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="sys_dict-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Dict> queryById(@RequestParam(name="id") String id) {
		Dict dict = sysDictService.getById(id);
		return Result.ok(dict);
	}

}
