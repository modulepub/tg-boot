package pub.module.ocr.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.module.web.vo.Result;
import pub.module.ocr.api.service.BizBankInfoService;

import jakarta.annotation.Resource;

/**
 * 综合工具
 *
 * @author panzhen
 */
@Tag(name = "工具")
@Slf4j
@RestController
@RequestMapping("/bank")
public class BankInfoController {

    @Resource
    BizBankInfoService bizBankInfoService;

    @Schema(description = "银行信息获取通过银行卡号")
    @Data
    public static class GetBankInfoByBankCardNoVO {

        @Schema(description = "银行卡号")
        String bankCardNo;

    }

    @Operation(summary = "通过银行卡号获取银行卡信息")
    @PostMapping(value = "/getBankInfoByBankCardNo")
    public Result<BizBankInfoService.BankInfo> bankInfo(GetBankInfoByBankCardNoVO getBankInfoByBankCardNoVO) {
        BizBankInfoService.BankInfo result = bizBankInfoService.getBankInfoByBankCardNo(getBankInfoByBankCardNoVO.getBankCardNo());
        return Result.ok(result);
    }


}
