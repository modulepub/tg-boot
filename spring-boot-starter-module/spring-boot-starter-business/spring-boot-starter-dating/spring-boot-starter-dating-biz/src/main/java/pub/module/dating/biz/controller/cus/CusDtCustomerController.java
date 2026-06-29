package pub.module.dating.biz.controller.cus;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.dating.api.service.ApiDtCustomerProfileEditService;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.CusCityResidenceOptionDTO;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.dto.DtCustomerProfileEditDTO;
import pub.module.dating.crud.service.DtCustomerService;
import pub.module.system.api.util.UserUtil;


/**
 * 用户端-客户
 *
 * @author tg
 * 2026-02-01 10:25:44
 */
@Tag(name = "用户端-客户")
@RestController
@RequestMapping("/cus/customer")
@Slf4j
public class CusDtCustomerController {

    @Resource
    DtCustomerService customerService;
    @Resource
    ApiDtCustomerService apiDtCustomerService;
    @Resource
    ApiDtCustomerProfileEditService apiDtCustomerProfileEditService;

    @Operation(summary = "用户端-客户-获取当前客户信息")
    @GetMapping(value = "/getCurrCusInfo")
    public Result<pub.module.dating.api.service.dto.DtCustomerDTO> getCurrCusInfo() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        pub.module.dating.api.service.dto.DtCustomerDTO customer = apiDtCustomerService.getCusByUserCode(userCode);
        apiDtCustomerService.enrichMemberBenefitDayUsage(customer, userCode);
        apiDtCustomerService.enrichProfileCompletenessRate(customer);
        return Result.ok(customer);
    }

    @Operation(summary = "用户端-客户-按昵称精确搜索嘉宾（可返回多条）")
    @GetMapping(value = "/searchByCusNickName")
    public Result<List<DtCustomerDTO>> searchByCusNickName(@RequestParam(name = "cusNickName") String cusNickName) {
        return Result.ok(apiDtCustomerService.listCusByNickNameExact(cusNickName));
    }

    @Operation(summary = "用户端-客户-城市列表（按生活城市编码分组，含名称）")
    @GetMapping(value = "/getCitys")
    public Result<List<CusCityResidenceOptionDTO>> getCitys() {
        return Result.ok(customerService.listDistinctResidenceCities());
    }

    @Operation(summary = "用户端-客户-获取当前资料编辑记录（无记录时同步 customer）")
    @GetMapping(value = "/getCurrProfileEdit")
    public Result<DtCustomerProfileEditDTO> getCurrProfileEdit() {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        DtCustomerProfileEditDTO dto = apiDtCustomerProfileEditService.getCurrProfileEdit(userCode);
        apiDtCustomerService.enrichMemberBenefitDayUsage(dto, userCode);
        apiDtCustomerService.enrichProfileCompletenessRate(dto);
        return Result.ok(dto);
    }

    @Operation(summary = "用户端-客户-保存资料编辑（新增记录，审核通过后同步 customer）")
    @PostMapping(value = "/saveCurrProfileEdit")
    public Result<DtCustomerProfileEditDTO> saveCurrProfileEdit(@RequestBody(required = false) DtCustomerDTO body) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        DtCustomerProfileEditDTO dto = apiDtCustomerProfileEditService.saveCurrProfileEdit(userCode, body);
        apiDtCustomerService.enrichMemberBenefitDayUsage(dto, userCode);
        apiDtCustomerService.enrichProfileCompletenessRate(dto);
        return Result.ok(dto);
    }

    @Operation(summary = "用户端-客户-编辑客户信息（已切换为资料编辑审核流程，同 saveCurrProfileEdit）")
    @PostMapping(value = "/editCurrCusInfo")
    public Result<DtCustomerProfileEditDTO> editCurrCusInfo(@RequestBody(required = false) DtCustomerDTO body) {
        String userCode = UserUtil.getCurrentSysUser().getUserCode();
        DtCustomerProfileEditDTO dto = apiDtCustomerProfileEditService.saveCurrProfileEdit(userCode, body);
        apiDtCustomerService.enrichMemberBenefitDayUsage(dto, userCode);
        apiDtCustomerService.enrichProfileCompletenessRate(dto);
        return Result.ok(dto);
    }
}