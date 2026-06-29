package pub.module.dating.biz.controller.pub;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.enums.StatusCodeEnum;
import pub.module.dating.crud.entity.DtMatchmaker;
import pub.module.dating.crud.service.DtMatchmakerService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;

import java.util.List;


/**
 * 公开-红娘信息
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
@Tag(name = "公开-小程序端红娘信息")
@RestController
@RequestMapping("/pub/dating/dtMatchmaker")
@Slf4j
public class PubDtMatchmakerController {
    @Resource
    private DtMatchmakerService dtMatchmakerService;


    @Operation(summary = "公开-红娘列表-红娘所在城市分组列表（去重，供筛选）")
    @GetMapping(value = "/listMkCityNames")
    public Result<List<String>> listMkCityNames() {
        return Result.ok(dtMatchmakerService.listDistinctMkCityNames());
    }

    @Operation(summary = "公开-红娘信息分页列表查询")
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

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class DtMatchmakerResVO extends DtMatchmaker {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class DtMatchmakerReqVO extends DtMatchmaker {

        private String mkCode;
    }

    @Operation(summary = "公开-红娘信息主页")
    @PostMapping(value = "/info")
    public Result<DtMatchmakerResVO> info(@RequestBody DtMatchmakerReqVO dtMatchmakerReqVO) {
        DtMatchmaker dtMatchmakerEntity = dtMatchmakerService.getByCode(dtMatchmakerReqVO.getMkCode());
        if (dtMatchmakerEntity == null || !StatusCodeEnum.isYesValue(dtMatchmakerEntity.getMkIdentityStatusCode())) {
            return Result.error("该红娘的资质正在审核中，看看别人吧");
        }
        DtMatchmakerResVO result= BeanUtil.copyProperties(dtMatchmakerEntity,DtMatchmakerResVO.class);
        return Result.ok(result);
    }

    @Operation(summary = "公开-通过红娘编码查询红娘信息")
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
}