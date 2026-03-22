package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;
import pub.module.dating.curd.entity.DtMatch;
import pub.module.dating.curd.service.DtMatchService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;


/**
 * 牵线申请
 * @author tg
 * @since 2025-07-21
 * @version V1.0
 */
@Tag(name ="牵线申请")
@RestController
@RequestMapping("/cus/dating/dtMatch")
@Slf4j
public class CusDtMatchController {
	@Resource
	private DtMatchService dtMatchApplicationService;

	
	@Operation(summary = "牵线申请-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DtMatch>> queryPageList(DtMatch dtMatch,
                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        dtMatch.setMtMkSysUserCode(UserUtil.getCurrentSysUser().getUserCode());
		QueryWrapper<DtMatch> queryWrapper = WebQueryUtil.buildQuery(dtMatch);
		Page<DtMatch> page = new Page<>(pageNo, pageSize);
		IPage<DtMatch> pageList = dtMatchApplicationService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary = "牵线申请-申请")
	@PostMapping(value = "/apply")
	public Result<DtMatch> reply(@RequestBody DtMatch dtMatch) {
        UserDTO sysUser = UserUtil.getCurrentSysUser();
        dtMatch.setMtProcessCode(sysUser.getUserCode());
        dtMatchApplicationService.save(dtMatch);
		return Result.ok(dtMatch);
	}

}
