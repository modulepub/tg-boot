package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pub.module.dating.api.service.dto.DtMatchDTO;
import pub.module.dating.api.service.dto.DtMatchUpdateRelationProgressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.ApiDtMatchService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import pub.module.dating.crud.entity.DtMatch;
import pub.module.dating.crud.service.DtMatchService;
import pub.module.system.api.util.UserUtil;

import jakarta.annotation.Resource;


/**
 * 牵线申请
 *
 * @author tg
 * @version V1.0
 * @since 2025-07-21
 */
@Tag(name = "用户端-牵线申请")
@RestController
@RequestMapping("/cus/dating/dtMatch")
@Slf4j
public class CusDtMatchController {
    @Resource
    private DtMatchService dtMatchService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private ApiDtMatchService apiDtMatchService;


    @Operation(summary = "用户端-我申请牵线的分页列表")
    @GetMapping(value = "/myPursuedlist")
    public Result<IPage<DtMatch>> queryPageList(DtMatch dtMatch,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        QueryWrapper<DtMatch> queryWrapper = WebQueryUtil.buildQuery(dtMatch);
        queryWrapper.lambda().eq(DtMatch::getMtPursuingCusCode, customerDto.getCusCode());
        Page<DtMatch> page = new Page<>(pageNo, pageSize);
        IPage<DtMatch> pageList = dtMatchService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-牵线申请-申请")
    @PostMapping(value = "/apply")
    public Result<DtMatchDTO> reply(@RequestBody DtMatchDTO dtMatch) {
        DtMatchDTO saved;
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        try {
            saved = apiDtMatchService.apply(dtMatch, userDTO.getUserCode());
        } catch (IllegalArgumentException e) {
			return Result.error(e.getMessage());
        }

        return Result.ok(saved);
    }

    @Operation(summary = "红娘工作台-牵线申请分页列表")
    @GetMapping(value = "/mkList")
    public Result<IPage<DtMatchDTO>> mkList(
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        IPage<DtMatchDTO> pageList = apiDtMatchService.listForMatchmaker(userDTO.getUserCode(), pageNo, pageSize);
        return Result.ok(pageList);
    }

    @Operation(summary = "红娘工作台-更新牵线关系进度及截图")
    @PostMapping(value = "/updateRelationProgress")
    public Result<DtMatchDTO> updateRelationProgress(@RequestBody DtMatchUpdateRelationProgressVO vo) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtMatchDTO updated = apiDtMatchService.updateRelationProgress(vo, userDTO.getUserCode());
        return Result.ok(updated);
    }

}
