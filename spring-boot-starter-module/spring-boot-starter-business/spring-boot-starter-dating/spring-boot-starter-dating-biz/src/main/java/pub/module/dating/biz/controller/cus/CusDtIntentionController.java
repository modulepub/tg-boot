package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.api.service.BizDtIntentionService;
import pub.module.dating.api.service.dto.DtIntentionDTO;
import pub.module.dating.curd.entity.DtIntention;
import pub.module.dating.curd.service.DtIntentionService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;

import jakarta.annotation.Resource;



/**
 * 相亲意向
 * @author tg
 * @since 2025-05-27
 * @version V1.0
 */
@Tag(name ="相亲意向")
@RestController
@RequestMapping("/cus/dtIntention")
@Slf4j
public class CusDtIntentionController {
	@Resource
	private DtIntentionService dtIntentionService;
	@Resource
	private BizDtIntentionService bizDtIntentionService;

	@Operation(summary = "相亲意向-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DtIntention>> queryPageList(DtIntention dtIntention,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<DtIntention> queryWrapper = WebQueryUtil.buildQuery(dtIntention);
		queryWrapper.lambda().eq(DtIntention::getIntentionSysUserCode, UserUtil.getCurrentSysUser().getUserCode());
		Page<DtIntention> page = new Page<>(pageNo, pageSize);
		IPage<DtIntention> pageList = dtIntentionService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	
	@Operation(summary = "相亲意向-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody DtIntention dtIntention) {
		if(!UserUtil.getCurrentSysUser().getUserCode().equals(dtIntention.getIntentionSysUserCode())){
			throw new RuntimeException("只能编辑自己的相亲意向！");
		}
		dtIntentionService.updateById(dtIntention);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary = "相亲意向-通过code查询")
	@GetMapping(value = "/queryByCode")
	public Result<DtIntention> queryById(@RequestParam(name="code") String code) {
		DtIntention dtIntention = dtIntentionService.getOne(new QueryWrapper<DtIntention>().lambda().eq(DtIntention::getIntentionCode, code), false);
		return Result.ok(dtIntention);
	}

	@Operation(summary = "相亲意向-获取上一次相亲意向")
	@GetMapping(value = "/getLastDtIntention")
	public Result<DtIntentionDTO> getLastDtIntention() {
        UserDTO sysUser = UserUtil.getCurrentSysUser();
		DtIntentionDTO dtIntention = bizDtIntentionService.getLastIntention(sysUser.getUserCode());
		return Result.ok(dtIntention);
	}

	@Operation(summary = "相亲意向-用戶首次初始化")
	@PostMapping(value = "/initDtIntention")
	public Result<DtIntentionDTO> initDtIntention(@RequestBody DtIntentionDTO dtIntention) {
        UserDTO sysUser = UserUtil.getCurrentSysUser();
		dtIntention.setDtIntentionSysUserCode(sysUser.getUserCode());
		bizDtIntentionService.initDtIntention(dtIntention);
		return Result.ok(dtIntention);
	}


}
