package pub.module.trade.biz.controller.pub;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pub.module.common.util.WebQueryUtil;
import pub.module.common.model.vo.Result;
import pub.module.trade.curd.entity.TdGoods;
import pub.module.trade.curd.service.ITdGoodsService;

import jakarta.annotation.Resource;



/**
 * 公开-公共商品业务控制器
 * 提供公共商品查询功能
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Tag(name ="公开-商品")
@RestController
@RequestMapping("/pub/trade/tradeGoods")
@Slf4j
public class PubBizTradeGoodsController {
	@Resource
	private ITdGoodsService tradeGoodsService;
	
	@Operation(summary = "公开-商品-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TdGoods>> queryPageList(TdGoods tdGoods,
                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
		QueryWrapper<TdGoods> queryWrapper = WebQueryUtil.buildQuery(tdGoods);
		Page<TdGoods> page = new Page<>(pageNo, pageSize);
		IPage<TdGoods> pageList = tradeGoodsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}


    @Operation(summary = "公开-商品-根据商品编码查询详情")
    @GetMapping(value = "/queryByCode")
    public Result<TdGoods> queryByCode(@RequestParam("tdGdCode") String tdGdCode) {
        QueryWrapper<TdGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TdGoods::getTdGdCode, tdGdCode);
        TdGoods result = tradeGoodsService.getOne(queryWrapper,false);
        return Result.ok(result);
    }

}
