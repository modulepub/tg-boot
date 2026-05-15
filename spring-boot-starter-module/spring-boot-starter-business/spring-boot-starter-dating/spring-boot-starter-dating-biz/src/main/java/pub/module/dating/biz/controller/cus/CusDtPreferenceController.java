package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.util.WebQueryUtil;
import pub.module.customer.api.service.ApiCustomerService;
import pub.module.customer.api.service.dto.CustomerDTO;
import pub.module.dating.api.constants.DtLikeDegreeCodeEnum;
import pub.module.dating.api.service.ApiDtPreferenceService;
import pub.module.dating.curd.entity.DtPreference;
import pub.module.dating.curd.service.DtPreferenceService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;
import pub.module.common.model.vo.Result;


/**
 * 用户端-偏好
 *
 * @author tg
 * 2026-03-31 02:10:33
 */
@Tag(name = "用户端-偏好")
@RestController
@RequestMapping("/cus/dating/dtPreference")
@Slf4j
public class CusDtPreferenceController {
    @Resource
    private ApiDtPreferenceService apiDtPreferenceService;
    @Resource
    private DtPreferenceService dtPreferenceService;
    @Resource
    private ApiCustomerService apiCustomerService;

    @Operation(summary = "用户端-我喜欢的列表")
    @GetMapping(value = "/myLikeList")
    public Result<IPage<DtPreference>> myLikeList(DtPreference dtPreference,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        CustomerDTO customerDTO = apiCustomerService.getCusByUserCode(userDTO.getUserCode());
        QueryWrapper<DtPreference> queryWrapper = WebQueryUtil.buildQuery(dtPreference);
        queryWrapper.lambda()
                .eq(DtPreference::getPreferenceCusCode, customerDTO.getCusCode())
                .eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.like.getCode());
        Page<DtPreference> page = new Page<>(pageNo, pageSize);
        IPage<DtPreference> pageList = dtPreferenceService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-喜欢我的列表")
    @GetMapping(value = "/likeMeList")
    public Result<IPage<DtPreference>> likeMeList(DtPreference dtPreference,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        CustomerDTO customerDTO = apiCustomerService.getCusByUserCode(userDTO.getUserCode());
        QueryWrapper<DtPreference> queryWrapper = WebQueryUtil.buildQuery(dtPreference);
        queryWrapper.lambda()
                .eq(DtPreference::getPreferenceTargetCusCode, customerDTO.getCusCode())
                .eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.like.getCode());
        Page<DtPreference> page = new Page<>(pageNo, pageSize);
        IPage<DtPreference> pageList = dtPreferenceService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-偏好添加")
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody DtPreference dtPreference) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        CustomerDTO customerDTO = apiCustomerService.getCusByUserCode(userDTO.getUserCode());
        apiDtPreferenceService.saveOrUpdate(customerDTO.getCusCode(), dtPreference.getPreferenceTargetCusCode(), dtPreference.getPreferenceLikeStatusCode());
        return Result.ok("保存成功！");
    }
}