package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.ApiDtContactApplyService;
import pub.module.dating.api.service.dto.DtContactApplyDTO;
import pub.module.dating.crud.entity.DtContactApply;
import pub.module.dating.crud.service.DtContactApplyService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * 用户端-联系人申请
 *
 * @author tg
 * 2026-05-03 03:39:43
 */
@Tag(name = "用户端-联系人申请")
@RestController
@RequestMapping("/cus/dating/dtContactApply")
@Slf4j
public class CusDtContactApplyController {
    @Resource
    private DtContactApplyService dtContactApplyService;
    @Resource
    private ApiDtContactApplyService apiDtContactApplyService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;

    @Operation(summary = "用户端-我发起的联系人申请-分页列表查询")
    @GetMapping(value = "/applyListByMe")
    public Result<IPage<DtContactApply>> queryPageList(DtContactApply dtContactApply,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DtContactApply> queryWrapper = WebQueryUtil.buildQuery(dtContactApply);
        Page<DtContactApply> page = new Page<>(pageNo, pageSize);
        IPage<DtContactApply> pageList = dtContactApplyService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-对方发起的联系人申请-分页列表查询")
    @GetMapping(value = "/applyListByThem")
    public Result<IPage<DtContactApply>> applyList(DtContactApply dtContactApply,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        QueryWrapper<DtContactApply> queryWrapper = WebQueryUtil.buildQuery(dtContactApply);
        queryWrapper.lambda().eq(DtContactApply::getCusCode,customerDto.getCusCode());
        Page<DtContactApply> page = new Page<>(pageNo, pageSize);
        IPage<DtContactApply> pageList = dtContactApplyService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-发起联系人申请")
    @PostMapping(value = "/apply")
    public Result<String> apply(@RequestBody ApiDtContactApplyService.ApplyDTO applyDTO) {
        apiDtContactApplyService.apply(applyDTO, UserUtil.getCurrentSysUser().getUserCode());
        return Result.ok("申请成功！");
    }

    @Operation(summary = "用户端-检查联系人申请状态")
    @PostMapping(value = "/check")
    public Result<DtContactApplyDTO> check(@RequestBody ApiDtContactApplyService.CheckDTO checkDTO) {
        DtContactApplyDTO result = apiDtContactApplyService.check(checkDTO, UserUtil.getCurrentSysUser().getUserCode());
        return Result.ok(result);
    }

    @Operation(summary = "用户端-联系人申请-申请通过")
    @PostMapping(value = "/pass")
    public Result<String> pass(@RequestBody ApiDtContactApplyService.PassDTO passDTO) {
        apiDtContactApplyService.pass(passDTO);
        return Result.ok("申请通过!");
    }

    @Operation(summary = "用户端-联系人申请-申请拒绝")
    @PostMapping(value = "/reject")
    public Result<String> reject(@RequestBody ApiDtContactApplyService.RejectDTO rejectDTO) {
        apiDtContactApplyService.reject(rejectDTO);
        return Result.ok("申请拒绝!");
    }
}