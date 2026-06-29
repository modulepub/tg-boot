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
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.constants.DtLikeDegreeCodeEnum;
import pub.module.dating.api.service.ApiDtPreferenceService;
import pub.module.dating.biz.service.DtPreferenceDisplayService;
import pub.module.dating.crud.entity.DtPreference;
import pub.module.dating.crud.service.DtPreferenceService;
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
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private DtPreferenceDisplayService dtPreferenceDisplayService;

    @Operation(summary = "用户端-我喜欢的列表")
    @GetMapping(value = "/myLikeList")
    public Result<IPage<DtPreference>> myLikeList(DtPreference dtPreference,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        QueryWrapper<DtPreference> queryWrapper = WebQueryUtil.buildQuery(dtPreference);
        queryWrapper.lambda()
                .eq(DtPreference::getPreferenceCusCode, customerDto.getCusCode())
                .eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.LIKE.getCode());
        Page<DtPreference> page = new Page<>(pageNo, pageSize);
        IPage<DtPreference> pageList = dtPreferenceService.page(page, queryWrapper);
        dtPreferenceDisplayService.enrichPeerDisplay(pageList, true);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-喜欢我的列表")
    @GetMapping(value = "/likeMeList")
    public Result<IPage<DtPreference>> likeMeList(DtPreference dtPreference,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        QueryWrapper<DtPreference> queryWrapper = WebQueryUtil.buildQuery(dtPreference);
        queryWrapper.lambda()
                .eq(DtPreference::getPreferenceTargetCusCode, customerDto.getCusCode())
                .eq(DtPreference::getPreferenceLikeStatusCode, DtLikeDegreeCodeEnum.LIKE.getCode());
        queryWrapper.apply(
                "exists (select 1 from dt_customer c where c.cus_code = dt_preference.preference_cus_code"
                        + " and c.cus_comlete_profile_status_code = {0} and c.deleted = {1})",
                StatusCodeEnum.YES.getCode(), StatusCodeEnum.NO.getCode());
        Page<DtPreference> page = new Page<>(pageNo, pageSize);
        IPage<DtPreference> pageList = dtPreferenceService.page(page, queryWrapper);
        dtPreferenceDisplayService.enrichPeerDisplay(pageList, false);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-查询对某嘉宾的偏好")
    @GetMapping(value = "/getByTargetCusCode")
    public Result<DtPreference> getByTargetCusCode(
            @RequestParam(name = "preferenceTargetCusCode") String preferenceTargetCusCode) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        DtPreference pref = dtPreferenceService.getOne(new QueryWrapper<DtPreference>().lambda()
                .eq(DtPreference::getPreferenceCusCode, customerDto.getCusCode())
                .eq(DtPreference::getPreferenceTargetCusCode, preferenceTargetCusCode), false);
        return Result.ok(pref);
    }

    @Operation(summary = "用户端-偏好添加")
    @PostMapping(value = "/save")
    public Result<String> save(@RequestBody DtPreference dtPreference) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        apiDtPreferenceService.saveOrUpdate(customerDto.getCusCode(), dtPreference.getPreferenceTargetCusCode(), dtPreference.getPreferenceLikeStatusCode().getCode());
        return Result.ok("保存成功！");
    }
}