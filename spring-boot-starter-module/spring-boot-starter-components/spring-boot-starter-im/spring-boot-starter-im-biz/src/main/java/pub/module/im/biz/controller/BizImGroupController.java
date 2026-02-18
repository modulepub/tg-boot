package pub.module.im.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.vo.Result;
import pub.module.im.api.service.BizImGroupService;
import pub.module.im.curd.entity.ImGroup;
import pub.module.im.curd.service.IImGroupService;
import pub.module.system.api.service.ApiSysUserService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;

/**
 * 融云即时通讯
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */

@Tag(name="融云即时通讯")
@RestController
@RequestMapping("/im/biz/imGroup")
@Slf4j
public class BizImGroupController {
	@Resource
	private IImGroupService imGroupService;
	@Resource
	BizImGroupService bizImGroupService;
	@Resource
    ApiSysUserService apiSysUserService;
	
	@Operation(summary="即时通讯群组-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ImGroup>> queryPageList(
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<ImGroup> queryWrapper = new QueryWrapper<>();
        String inSQL = "select im_group_code from im_group_member where sys_user_code = '{{userCode}}'";
        inSQL = inSQL.replace("{{userCode}}", UserUtil.getCurrentSysUser().getUserCode());
        queryWrapper.lambda().inSql(ImGroup::getImGroupCode, inSQL);
		Page<ImGroup> page = new Page<>(pageNo, pageSize);
		IPage<ImGroup> pageList = imGroupService.page(page, queryWrapper);
		for(ImGroup item:pageList.getRecords()){
            UserDTO sysUser = apiSysUserService.getUserByUserCode(item.getImGroupBelongSysUserCode());
			item.setImGroupBelongSysUser(sysUser);
		}
		return Result.ok(pageList);
	}

	@Operation(summary="即时通讯群组-获取不繁忙的客服群组")
	@GetMapping(value = "/getKxGroup")
	public Result<ImGroup> getKxGroup( ){
		ImGroup imGroup = bizImGroupService.getKxGroup(UserUtil.getCurrentSysUser().getUserCode());
		return Result.ok(imGroup);
	}


}
