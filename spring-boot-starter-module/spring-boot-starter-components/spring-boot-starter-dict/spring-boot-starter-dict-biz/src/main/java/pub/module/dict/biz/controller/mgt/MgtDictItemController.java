package pub.module.dict.biz.controller.mgt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dict.curd.entity.DictItem;
import pub.module.dict.curd.service.DictItemService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

import java.util.Collection;

/**
 * sys_dict_item
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */

@Tag(name="管理端-sys_dict_item")
@RestController
@RequestMapping("/mgt/sysDictItem")
@Slf4j
public class MgtDictItemController {
	@Resource
	private DictItemService sysDictItemService;
	
	@Operation(summary="管理端-sys_dict_item-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DictItem>> queryPageList(DictItem dictItem,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<DictItem> queryWrapper = WebQueryUtil.buildQuery(dictItem);
		Page<DictItem> page = new Page<>(pageNo, pageSize);
		IPage<DictItem> pageList = sysDictItemService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary="管理端-sys_dict_item-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody DictItem dictItem) {
		sysDictItemService.save(dictItem);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="管理端-sys_dict_item-编辑")
	@PostMapping(value = "/edit")
	public Result<String> edit(@RequestBody DictItem dictItem) {
		sysDictItemService.updateById(dictItem);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="管理端-sys_dict_item-通过id删除")
	@PostMapping(value = "/delete")
	public Result<String> delete(@RequestBody Collection<String> ids) {
		sysDictItemService.removeBatchByIds(ids);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="管理端-sys_dict_item-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DictItem> queryById(@RequestParam(name="id") String id) {
		DictItem dictItem = sysDictItemService.getById(id);
		return Result.ok(dictItem);
	}

}
