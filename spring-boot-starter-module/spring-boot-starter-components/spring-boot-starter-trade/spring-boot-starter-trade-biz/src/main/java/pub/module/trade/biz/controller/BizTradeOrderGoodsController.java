package pub.module.trade.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.system.api.service.dto.UserDTO;
import pub.module.web.util.WebQueryUtil;
import pub.module.web.vo.Result;
import pub.module.system.api.util.UserUtil;
import pub.module.trade.curd.entity.TdOrderGoods;
import pub.module.trade.curd.service.ITdOrderGoodsService;

import jakarta.annotation.Resource;

import java.util.Arrays;


/**
 * 订单商品业务控制器
 * 处理订单商品相关的业务逻辑请求
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name ="订单商品")
@RestController
@RequestMapping("/trade/biz/tradeOrderGoods")
@Slf4j
public class BizTradeOrderGoodsController {
	@Resource
	private ITdOrderGoodsService tradeOrderGoodsService;
	
	@Operation(summary = "订单商品-分页列表查询订单商品-分页列表查询")
	@GetMapping(value = "/listByVendor")
	public Result<IPage<TdOrderGoods>> queryPageList(TdOrderGoods tdOrderGoods,
                                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
        UserDTO sysUser = UserUtil.getCurrentSysUser();
		QueryWrapper<TdOrderGoods> queryWrapper = WebQueryUtil.buildQuery(tdOrderGoods);
        queryWrapper.lambda().eq(TdOrderGoods::getTdGdSysUserCode, sysUser.getUserCode());
		Page<TdOrderGoods> page = new Page<>(pageNo, pageSize);
		IPage<TdOrderGoods> pageList = tradeOrderGoodsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

    @Operation(summary = "订单商品-分页列表查询订单商品-分页列表查询")
    @GetMapping(value = "/listByUser")
    public Result<IPage<TdOrderGoods>> listByUser(TdOrderGoods tdOrderGoods,
                                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize
                                                        ) {
        QueryWrapper<TdOrderGoods> queryWrapper = WebQueryUtil.buildQuery(tdOrderGoods);
        queryWrapper.lambda().eq(TdOrderGoods::getTdOdSysUserCode, UserUtil.getCurrentSysUser().getUserCode());
        Page<TdOrderGoods> page = new Page<>(pageNo, pageSize);
        IPage<TdOrderGoods> pageList = tradeOrderGoodsService.page(page, queryWrapper);
        return Result.ok(pageList);
    }
	
	@Operation(summary = "订单商品-添加订单商品-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TdOrderGoods tdOrderGoods) {
		tradeOrderGoodsService.save(tdOrderGoods);
		return Result.ok("添加成功！");
	}
	
	@Operation(summary = "订单商品-编辑订单商品-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TdOrderGoods tdOrderGoods) {
		tradeOrderGoodsService.updateById(tdOrderGoods);
		return Result.ok("编辑成功!");
	}
	
	@Operation(summary = "订单商品-通过id删除订单商品-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id") String id) {
		tradeOrderGoodsService.removeById(id);
		return Result.ok("删除成功!");
	}
	
	@Operation(summary = "订单商品-批量删除订单商品-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids") String ids) {
		this.tradeOrderGoodsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}
	
	@Operation(summary = "订单商品-通过id查询订单商品-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TdOrderGoods> queryById(@RequestParam(name="id") String id) {
		TdOrderGoods tdOrderGoods = tradeOrderGoodsService.getById(id);
		return Result.ok(tdOrderGoods);
	}

}
