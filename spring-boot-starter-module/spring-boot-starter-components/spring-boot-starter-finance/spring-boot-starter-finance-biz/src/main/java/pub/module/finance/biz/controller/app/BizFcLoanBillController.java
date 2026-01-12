package pub.module.finance.biz.controller.app;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;
import pub.module.finance.api.constants.FcLoanBillDueStatusCodeEnum;
import pub.module.finance.curd.entity.FcLoanBill;
import pub.module.finance.curd.service.IFcLoanBillService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;



@Tag(name="信用借贷")
@RestController
@RequestMapping("/finance/biz/fcLoanBill")
@Slf4j
public class BizFcLoanBillController {
	@Resource
	private IFcLoanBillService fcLoanBillService;
	
	@Operation(summary="我的借贷分期账单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FcLoanBill>> queryPageList(FcLoanBill fcLoanBill,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<FcLoanBill> queryWrapper = WebQueryUtil.buildQuery(fcLoanBill);
		queryWrapper.lambda().eq(FcLoanBill::getUserCode, UserUtil.getCurrentSysUser().getUserCode());
		Page<FcLoanBill> page = new Page<>(pageNo, pageSize);
		IPage<FcLoanBill> pageList = fcLoanBillService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
	@Data
	@Schema(description="当期+逾期")
	public static class FcLoanBillVO {

		@Schema(description = "产品类型")
		private java.lang.String fcProductTypeCode;

	}
	@Operation(summary="我的借贷分期账单-当期+逾期分页列表查询")
	@GetMapping(value = "/listDue")
	public Result<IPage<FcLoanBill>> listDue(FcLoanBillVO fcLoanBillVO,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		FcLoanBill fcLoanBill = BeanUtil.copyProperties(fcLoanBillVO, FcLoanBill.class);
		QueryWrapper<FcLoanBill> queryWrapper = WebQueryUtil.buildQuery(fcLoanBill);
		queryWrapper.lambda().eq(FcLoanBill::getUserCode, UserUtil.getCurrentSysUser().getUserCode());
		queryWrapper.lambda().and(
				q -> q.eq(FcLoanBill::getFcLoanBillDueStatusCode, FcLoanBillDueStatusCodeEnum.YES.getCode()).or()
						.eq(FcLoanBill::getFcLoanBillOverdueStatusCode, FcLoanBillDueStatusCodeEnum.YES.getCode()));
		queryWrapper.lambda().orderByAsc(FcLoanBill::getFcLoanBillDueStatusCode);
		Page<FcLoanBill> page = new Page<>(pageNo, pageSize);
		IPage<FcLoanBill> pageList = fcLoanBillService.page(page, queryWrapper);
		return Result.ok(pageList);
	}


	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = false)
	public static class GetFcLoanBillByCodeVO  {
		/**账单编码*/
		@Schema(description = "账单编码")
		private java.lang.String fcLoanBillCode;
	}
	@Operation(summary="我的借贷分期账单详情-通过账单编码查询")
	@GetMapping(value = "/getFcLoanBillByCode")
	public Result<FcLoanBill> getFcLoanBillByCode(GetFcLoanBillByCodeVO getFcLoanBillByCodeVO) {
		QueryWrapper<FcLoanBill> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(FcLoanBill::getFcLoanBillCode,getFcLoanBillByCodeVO.getFcLoanBillCode());
		FcLoanBill fcLoanBill = fcLoanBillService.getOne(queryWrapper,false);
		return Result.ok(fcLoanBill);
	}
	


}
