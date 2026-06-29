package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.lang.Assert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.dating.api.service.ApiDtCustomerService;
import pub.module.dating.api.service.dto.DtCustomerDTO;
import pub.module.dating.api.service.ApiDtCusMatchmakerRelService;
import pub.module.dating.api.service.dto.CusMkFollowDTO;
import pub.module.dating.api.service.dto.CusMkRelShowStatusUpdateDTO;
import pub.module.dating.crud.entity.DtCusMatchmakerRel;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtCusMatchmakerRelService;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;


/**
 * 用户端-客户红娘关系
 *
 * @author tg
 * 2026-03-25 00:36:20
 */
@Tag(name = "用户端-客户红娘关系")
@RestController
@RequestMapping("/cus/dating/dtCusMatchmakerRel")
@Slf4j
public class CusDtCusMatchmakerRelController {
    @Resource
    private DtCusMatchmakerRelService dtCusMatchmakerRelService;
    @Resource
    private ApiDtCustomerService apiDtCustomerService;
    @Resource
    private ApiDtCusMatchmakerRelService apiDtCusMatchmakerRelService;
    @Resource
    private DtMatchmakerService dtMatchmakerService;


    @Operation(summary = "用户端-我的助力列表接口")
    @GetMapping(value = "/myMatchmakerList")
    public Result<IPage<DtCusMatchmakerRel>> queryPageList(DtCusMatchmakerRel dtCusMatchmakerRel,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtCustomerDTO customerDto = apiDtCustomerService.getCusByUserCode(userDTO.getUserCode());
        QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
        queryWrapper.lambda().eq(DtCusMatchmakerRel::getCusCode, customerDto.getCusCode());
        Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
        IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-更新客户红娘关系主页展示开关")
    @PostMapping(value = "/updateCusMkRelShowStatus")
    public Result<String> updateCusMkRelShowStatus(@RequestBody CusMkRelShowStatusUpdateDTO dto) {
        apiDtCusMatchmakerRelService.updateCusMkRelShowStatus(dto);
        return Result.ok("已更新");
    }

    @Operation(summary = "用户端-关注红娘（建立客户红娘关系，幂等）")
    @PostMapping(value = "/followMatchmaker")
    public Result<String> followMatchmaker(@RequestBody CusMkFollowDTO dto) {
        Assert.notNull(dto, "参数不能为空");
        Assert.notBlank(dto.getMkCode(), "红娘编码不能为空");
        apiDtCusMatchmakerRelService.followMatchmakerByMkCode(dto.getMkCode().trim());
        return Result.ok("关注成功");
    }

    @Operation(summary = "用户端-是否已关注该红娘")
    @GetMapping(value = "/followStatus")
    public Result<Boolean> followStatus(@RequestParam(name = "mkCode") String mkCode) {
        Assert.notBlank(mkCode, "红娘编码不能为空");
        return Result.ok(apiDtCusMatchmakerRelService.isFollowedMatchmaker(mkCode.trim()));
    }

    @Operation(summary = "红娘工作台-客户关系分页列表")
    @GetMapping(value = "/mkCustomerList")
    public Result<IPage<DtCusMatchmakerRel>> mkCustomerList(
            DtCusMatchmakerRel dtCusMatchmakerRel,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        UserDTO userDTO = UserUtil.getCurrentSysUser();
        DtMatchmaker matchmaker = dtMatchmakerService.getOne(new QueryWrapper<DtMatchmaker>().lambda()
                .eq(DtMatchmaker::getMkUserCode, userDTO.getUserCode()), false);
        if (matchmaker != null) {
            QueryWrapper<DtCusMatchmakerRel> queryWrapper = WebQueryUtil.buildQuery(dtCusMatchmakerRel);
            queryWrapper.lambda()
                    .eq(DtCusMatchmakerRel::getMkCode, matchmaker.getMkCode())
                    .orderByDesc(DtCusMatchmakerRel::getCreateTime);
            Page<DtCusMatchmakerRel> page = new Page<>(pageNo, pageSize);
            IPage<DtCusMatchmakerRel> pageList = dtCusMatchmakerRelService.page(page, queryWrapper);
            return Result.ok(pageList);
        } else {
            return Result.ok(new Page<>());
        }

    }


}