package pub.module.finance.biz.controller.mgt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.web.vo.Result;
import pub.module.finance.api.service.BizFcCreditExtensionService;
import pub.module.finance.api.service.BizFcLoanService;

import jakarta.annotation.Resource;
import java.io.Serializable;

/**
 * 信用借贷管理
 * @author tg
 * @since 2025-10-02
 * @version V1.0
 */

@Tag(name="信用借贷管理")
@RestController
@RequestMapping("/finance/biz/mgt/fcLoan")
@Slf4j
public class BizMgtFcLoanController {

	@Resource
	private BizFcLoanService bizFcLoanService;
	@Resource
	private BizFcCreditExtensionService bizFcCreditExtensionService;


	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = false)
	public static  class PassVO implements Serializable {
		@Schema(description = "借贷编码")
		private String fcLoanCode;
    }

	@Operation(summary="信用借贷-现金借款审核通过")
	@PostMapping(value = "/cashLoanPass")
	public Result<?> cashLoanPass(@RequestBody PassVO passVO) {
		bizFcLoanService.cashLoanPass(passVO.getFcLoanCode());
		return Result.ok();
	}


	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = false)
	public static  class RejectVO implements Serializable {
		@Schema(description = "借贷编码")
		private String fcLoanCode;
	}
	//TODO 鉴权
	@Operation(summary="信用借贷-现金借款审核拒绝")
	@PostMapping(value = "/cashLoanReject")
	public Result<?> cashLoanReject(@RequestBody RejectVO rejectVO) {
		bizFcLoanService.cashLoanReject(rejectVO.getFcLoanCode());
		return Result.ok();
	}

	@Operation(summary="信用借贷-授信审核拒绝")
	@PostMapping(value = "/creditLoanReject")
	public Result<?> creditLoanReject(@RequestBody CdVO rejectVO) {
		bizFcCreditExtensionService.creditLoanReject(rejectVO.getFcCdExCode());
		return Result.ok();
	}
	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = false)
	public static  class CdVO implements Serializable {
		@Schema(description = "授信编码")
		private String fcCdExCode;
	}
	@Operation(summary="信用借贷-授信审核通过")
	@PostMapping(value = "/creditLoanPass")
	public Result<?> creditLoanPass(@RequestBody CdVO cdPassVO) {
		bizFcCreditExtensionService.creditLoanPass(cdPassVO.getFcCdExCode());
		return Result.ok();
	}

}
