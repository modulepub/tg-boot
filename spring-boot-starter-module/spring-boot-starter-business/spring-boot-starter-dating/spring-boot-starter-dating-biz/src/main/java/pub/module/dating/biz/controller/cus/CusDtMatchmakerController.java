package pub.module.dating.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.common.model.vo.Result;
import pub.module.common.util.WebQueryUtil;
import pub.module.dating.api.service.ApiDtMatchmakerService;
import pub.module.dating.api.service.dto.MatchmakerChannelsDTO;
import pub.module.dating.api.service.dto.MatchmakerChannelsUpdateVO;
import pub.module.dating.api.service.dto.MatchmakerQualificationApplyDTO;
import pub.module.dating.api.service.dto.MatchmakerQualificationApplySubmitVO;
import pub.module.dating.api.service.dto.MatchmakingCompanyOptionDTO;
import pub.module.dating.biz.service.InitGoodsService;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.service.ApiTdGoodsService;

import pub.module.system.api.service.dto.UserDTO;
import pub.module.system.api.util.UserUtil;

import java.util.List;

/**
 * 用户端-红娘信息
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
@Tag(name = "用户端-红娘信息")
@RestController
@RequestMapping("/cus/dating/dtMatchmaker")
@Slf4j
public class CusDtMatchmakerController {
    @Resource
    private DtMatchmakerService dtMatchmakerService;
    @Resource
    private ApiTdGoodsService apiTdGoodsService;
    @Resource
    private InitGoodsService initGoodsService;
    @Resource
    private ApiDtMatchmakerService apiDtMatchmakerService;


    @Operation(summary = "用户端-红娘列表-红娘所在城市分组列表（去重，供筛选）")
    @GetMapping(value = "/listMkCityNames")
    public Result<List<String>> listMkCityNames() {
        return Result.ok(dtMatchmakerService.listDistinctMkCityNames());
    }

    @Operation(summary = "用户端-红娘列表")
    @GetMapping(value = "/list")
    public Result<IPage<DtMatchmaker>> queryPageList(DtMatchmaker dtMatchmaker,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<DtMatchmaker> queryWrapper = WebQueryUtil.buildQuery(dtMatchmaker);
        queryWrapper.eq("mk_identity_status_code", StatusCodeEnum.YES.getCode());
        Page<DtMatchmaker> page = new Page<>(pageNo, pageSize);
        IPage<DtMatchmaker> pageList = dtMatchmakerService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-通过红娘编码查询红娘信息")
    @GetMapping(value = "/queryByMkCode")
    public Result<DtMatchmaker> queryByMkCode(@RequestParam(name = "mkCode") String mkCode) {
        DtMatchmaker dtMatchmaker = dtMatchmakerService.getByCode(mkCode);
        if (dtMatchmaker == null) {
            return Result.error("红娘不存在或已下架");
        }
        if (!StatusCodeEnum.isYesValue(dtMatchmaker.getMkIdentityStatusCode())) {
            return Result.error("该红娘的资质正在审核中，看看别人吧");
        }
        return Result.ok(dtMatchmaker);
    }

    @Operation(summary = "用户端-通过红娘编码查询红娘售卖的服务")
    @GetMapping(value = "/queryGoodsByMkCode")
    public Result<List<TdGoodsDTO>> queryGoodsByMkCode(@RequestParam(name = "mkCode") String mkCode) {
        DtMatchmaker dtMatchmaker = dtMatchmakerService.getByCode(mkCode);
        if (dtMatchmaker == null) {
            return Result.error("红娘不存在或已封号");
        }
        if (!StatusCodeEnum.isYesValue(dtMatchmaker.getMkIdentityStatusCode())) {
            return Result.error("该红娘的资质正在审核中，看看别人吧");
        }
        List<TdGoodsDTO> goodsDTOList = apiTdGoodsService.listByTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        return Result.ok(goodsDTOList);
    }

    @Operation(summary = "用户端-当前认证红娘初始化/更新默认服务商品")
    @PostMapping(value = "/initMyGoods")
    public Result<List<TdGoodsDTO>> initMyGoods() {
        UserDTO user = UserUtil.getCurrentSysUser();
        DtMatchmaker dtMatchmaker = dtMatchmakerService.getByUserCode(user.getUserCode());
        if (dtMatchmaker == null) {
            return Result.error("您还不是认证红娘");
        }
        if (!StatusCodeEnum.isYesValue(dtMatchmaker.getMkIdentityStatusCode())) {
            return Result.error("红娘资质审核中，暂无法初始化服务");
        }
        List<TdGoodsDTO> result = initGoodsService.initByMk(dtMatchmaker);
        return Result.ok(result);
    }

    @Operation(summary = "用户端-当前用户红娘资质申请信息")
    @GetMapping("/myQualificationApply")
    public Result<MatchmakerQualificationApplyDTO> myQualificationApply() {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakerService.getMyQualificationApply(user.getUserCode()));
    }

    @Operation(summary = "用户端-提交红娘资质申请")
    @PostMapping("/submitQualificationApply")
    public Result<MatchmakerQualificationApplyDTO> submitQualificationApply(@RequestBody MatchmakerQualificationApplySubmitVO vo) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakerService.submitQualificationApply(user.getUserCode(), vo));
    }

    @Operation(summary = "用户端-资质申请可选的已审核婚介公司")
    @GetMapping("/listCertifiedCompanies")
    public Result<List<MatchmakingCompanyOptionDTO>> listCertifiedCompanies() {
        return Result.ok(apiDtMatchmakerService.listCertifiedCompanies());
    }

    @Operation(summary = "用户端-当前认证红娘查询视频号配置")
    @GetMapping("/myChannels")
    public Result<MatchmakerChannelsDTO> myChannels() {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakerService.getMyChannels(user.getUserCode()));
    }

    @Operation(summary = "用户端-当前认证红娘修改视频号")
    @PostMapping("/updateMyChannels")
    public Result<MatchmakerChannelsDTO> updateMyChannels(@RequestBody MatchmakerChannelsUpdateVO vo) {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakerService.updateMyChannels(user.getUserCode(), vo.getMkChannelsFinderUserName()));
    }

    @Operation(summary = "用户端-当前认证红娘提交视频号审核")
    @PostMapping("/submitMyChannels")
    public Result<MatchmakerChannelsDTO> submitMyChannels() {
        UserDTO user = UserUtil.getCurrentSysUser();
        return Result.ok(apiDtMatchmakerService.submitMyChannels(user.getUserCode()));
    }


}