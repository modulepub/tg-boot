package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.vo.Result;
import pub.module.system.api.service.BizSysUserService;

import jakarta.annotation.Resource;


@Tag(name ="红娘信息")
@RestController
@RequestMapping("/dating/biz/matchmaker")
@Slf4j
public class CusDtMatchmakerController {
    @Resource
    private BizSysUserService bizSysUserService;


    @Operation(summary ="红娘-红娘详情")
    @GetMapping(value = "/queryBySysUserCode")
    public Result<UserDTO> dtMkSysUserCode(@RequestParam(name = "userCode") String userCode) {
        UserDTO dtMatchmaker = bizSysUserService.getUserByUserCode(userCode);
        return Result.ok(dtMatchmaker);
    }


    @Operation(summary = "红娘-添加客户")
    @PostMapping(value = "/add")
    public Result<UserDTO> addCustom(@RequestBody UserDTO dtCustomer) {
        bizSysUserService.addSysUser(dtCustomer);
        return Result.ok(dtCustomer);
    }

    @Operation(summary = "红娘-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UserDTO>> myCustomList(UserDTO dtCustomer,
                                               @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        IPage<UserDTO> pageList = bizSysUserService.page(dtCustomer, pageNo,pageSize);
        return Result.ok(pageList);
    }

    @Operation(summary = "红娘-我的客户分页列表查询")
    @GetMapping(value = "/myCusList")
    public Result<IPage<UserDTO>> myCusList(UserDTO dtCustomer,
                                            @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                            @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        IPage<UserDTO> pageList = bizSysUserService.page(dtCustomer, pageNo,pageSize);
        return Result.ok(pageList);
    }

    @Data
    public static class DeleteCustomerVO{
        String userCode;
    }

    @Operation(summary = "红娘-删除我的客户")
    @PostMapping(value = "/delete")
    public Result<String> delete(@RequestBody DeleteCustomerVO deleteCustomerVo) {
        bizSysUserService.deleteByCode(deleteCustomerVo.getUserCode());
        return Result.ok("删除成功!");
    }

    @Operation(summary = "红娘-编辑我的客户信息")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody UserDTO dtCustomer) {
        bizSysUserService.updateById(dtCustomer);
        return Result.ok("编辑成功!");
    }
}
