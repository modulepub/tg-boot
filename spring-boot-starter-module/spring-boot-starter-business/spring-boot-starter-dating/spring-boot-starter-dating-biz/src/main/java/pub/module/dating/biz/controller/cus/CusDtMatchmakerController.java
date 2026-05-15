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
import pub.module.dating.biz.service.InitGoodsService;
import pub.module.dating.curd.entity.DtMatchmaker;
import pub.module.dating.curd.service.DtMatchmakerService;
import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.service.ApiTdGoodsService;

import java.util.Collection;
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
        return Result.ok(dtMatchmaker);
    }

    @Operation(summary = "用户端-通过红娘编码查询红娘售卖的服务")
    @GetMapping(value = "/queryGoodsByMkCode")
    public Result<List<TdGoodsDTO>> queryGoodsByMkCode(@RequestParam(name = "mkCode") String mkCode) {
        DtMatchmaker dtMatchmaker = dtMatchmakerService.getByCode(mkCode);
        if (dtMatchmaker == null) {
            return Result.error("红娘不存在或已封号");
        }
        List<TdGoodsDTO> goodsDTOList = apiTdGoodsService.listByTdGdSysUserCode(dtMatchmaker.getMkUserCode());
        if(goodsDTOList.isEmpty()){
            goodsDTOList = initGoodsService.initByMk(dtMatchmaker);
        }
        return Result.ok(goodsDTOList);
    }


}