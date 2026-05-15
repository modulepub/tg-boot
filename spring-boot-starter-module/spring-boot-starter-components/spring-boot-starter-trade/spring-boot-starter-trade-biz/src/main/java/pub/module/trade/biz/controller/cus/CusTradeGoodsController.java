package pub.module.trade.biz.controller.cus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.trade.curd.service.ITdGoodsService;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import pub.module.system.api.util.UserUtil;
import pub.module.trade.curd.entity.TdGoods;

import jakarta.annotation.Resource;


/**
 * 商品
 * @author tg
 * @since 2025-08-17
 * @version V1.0
 */
@Tag(name ="用户端-商品")
@RestController
@RequestMapping("/cus/trade/tdGoods")
@Slf4j
public class CusTradeGoodsController {
	@Resource
	private ITdGoodsService tradeGoodsService;
	
	@Operation(summary = "用户端-商品-分页列表查询商品-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TdGoods>> queryPageList(TdGoods tdGoods,
                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdGoods> queryWrapper = WebQueryUtil.buildQuery(tdGoods);
		Page<TdGoods> page = new Page<>(pageNo, pageSize);
		IPage<TdGoods> pageList = tradeGoodsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}
    @Operation(summary = "用户端-商品-分页列表查询商品-分页列表查询")
    @GetMapping(value = "/listByTdGdSysUserCode")
    public Result<IPage<TdGoods>> listByTdGdSysUserCode(TdGoods tdGoods,
                                                        @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                        @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        QueryWrapper<TdGoods> queryWrapper = WebQueryUtil.buildQuery(tdGoods);
        queryWrapper.lambda().eq(TdGoods::getTdGdSysUserCode, UserUtil.getCurrentSysUser().getUserCode());
        Page<TdGoods> page = new Page<>(pageNo, pageSize);
        IPage<TdGoods> pageList = tradeGoodsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @Operation(summary = "用户端-商品-添加商品-添加")
    @PostMapping(value = "/add")
    public Result<TdGoods> add(@RequestBody TdGoods tdGoods) {
        tdGoods.setTdGdSysUserCode(UserUtil.getCurrentSysUser().getUserCode());
        tradeGoodsService.save(tdGoods);
        return Result.ok(tdGoods);
    }


    @Operation(summary = "用户端-商品-添加商品-添加")
    @GetMapping(value = "/getByCode")
    public Result<TdGoods> getByCode(@RequestParam("tdGdCode") String tdGdCode) {
        QueryWrapper<TdGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TdGoods::getTdGdCode, tdGdCode);
        TdGoods source = tradeGoodsService.getOne(queryWrapper,false);
        return Result.ok(source);
    }
    @Operation(summary = "用户端-商品-编辑商品-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody TdGoods tdGoods) {
        tradeGoodsService.updateById(tdGoods);
        return Result.ok("编辑成功!");
    }
}
