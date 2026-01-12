package pub.module.cms.curd.controller;
import java.util.Arrays;


import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import pub.module.cms.curd.entity.CmsChannel;
import pub.module.cms.curd.service.ICmsChannelService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

/**
 * CMS栏目
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */

@Tag(name="CMS栏目")
@RestController
@RequestMapping("/cms/curd/cmsChannel")
@Slf4j
public class CmsChannelController{
	@Resource
	private ICmsChannelService cmsChannelService;
	
	@Operation(summary="CMS栏目-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CmsChannel>> queryPageList(CmsChannel cmsChannel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<CmsChannel> queryWrapper = WebQueryUtil.buildQuery(cmsChannel);
		Page<CmsChannel> page = new Page<>(pageNo, pageSize);
        queryWrapper.lambda().and(cmsChannelLambdaQueryWrapper -> cmsChannelLambdaQueryWrapper.isNull(CmsChannel::getCcParentCode).or().eq(CmsChannel::getCcParentCode,""));
		IPage<CmsChannel> pageList = cmsChannelService.page(page, queryWrapper);
        for(CmsChannel item:pageList.getRecords()){
            List<CmsChannel> children = cmsChannelService.list(new QueryWrapper<CmsChannel>().lambda().eq(CmsChannel::getCcParentCode, item.getCcCode()));
            item.setChildren(children);
        }
		return Result.ok(pageList);
	}
	
	@Operation(summary="CMS栏目-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CmsChannel cmsChannel) {
		cmsChannelService.save(cmsChannel);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary="CMS栏目-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CmsChannel cmsChannel) {
		cmsChannelService.updateById(cmsChannel);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary="CMS栏目-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		cmsChannelService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary="CMS栏目-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.cmsChannelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary="CMS栏目-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CmsChannel> queryById(@RequestParam(name="id") String id) {
		CmsChannel cmsChannel = cmsChannelService.getById(id);
		return Result.ok(cmsChannel);
	}

}
