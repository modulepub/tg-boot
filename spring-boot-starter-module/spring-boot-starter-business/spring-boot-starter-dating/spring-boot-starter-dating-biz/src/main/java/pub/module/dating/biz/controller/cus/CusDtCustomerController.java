package pub.module.dating.biz.controller.cus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.web.vo.Result;
import pub.module.system.api.service.ApiSysUserService;

import jakarta.annotation.Resource;


@Tag(name = "客户")
@RestController
@RequestMapping("/cus/dating")
@Slf4j
public class CusDtCustomerController {
    @Resource
    ApiSysUserService apiSysUserService;

    @Operation(summary = "客户信息-通过客户编码查询")
    @GetMapping(value = "/queryCusInfoByCode")
    public Result<UserDTO> queryCusInfoByCode(@RequestParam("code") String code) {
        UserDTO dtCustomer = apiSysUserService.getUserByUserCode(code);
        return Result.ok(dtCustomer);
    }

    @Operation(summary = "红娘信息-通过客户编码查询")
    @GetMapping(value = "/selfInfo")
    public Result<UserDTO> selfInfo() {
        UserDTO dtCustomer = apiSysUserService.getUserByUserCode(UserUtil.getCurrentSysUser().getUserCode());
        return Result.ok(dtCustomer);
    }


}
