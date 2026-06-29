package pub.module.trade.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import pub.module.trade.api.dto.TdOrderGoodsDTO;
import pub.module.trade.api.service.ApiTdOrderGoodsService;
import pub.module.trade.crud.entity.TdOrderGoods;
import pub.module.trade.crud.service.ITdOrderGoodsService;

import java.util.Collections;
import java.util.List;

/**
 * 订单商品明细 Api 实现
 *
 * @author tg
 * @since 2026-05-25
 */
@Service
public class ApiTdOrderGoodsServiceImpl implements ApiTdOrderGoodsService {

    @Resource
    private ITdOrderGoodsService tradeOrderGoodsService;

    @Override
    public IPage<TdOrderGoodsDTO> pageByTdGdSysUserCode(String tdGdSysUserCode, Integer pageNo, Integer pageSize) {
        Assert.isTrue(StrUtil.isNotBlank(tdGdSysUserCode), "tdGdSysUserCode 不能为空");
        int pn = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int ps = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        Page<TdOrderGoods> page = new Page<>(pn, ps);
        QueryWrapper<TdOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .eq(TdOrderGoods::getTdGdSysUserCode, tdGdSysUserCode.trim())
            .orderByDesc(TdOrderGoods::getCreateTime);
        IPage<TdOrderGoods> pageList = tradeOrderGoodsService.page(page, queryWrapper);
        return pageList.convert(row -> BeanUtil.copyProperties(row, TdOrderGoodsDTO.class));
    }

    @Override
    public List<TdOrderGoodsDTO> listByTdGdSysUserCode(String tdGdSysUserCode) {
        if (StrUtil.isBlank(tdGdSysUserCode)) {
            return Collections.emptyList();
        }
        QueryWrapper<TdOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
            .eq(TdOrderGoods::getTdGdSysUserCode, tdGdSysUserCode.trim())
            .orderByDesc(TdOrderGoods::getCreateTime);
        return tradeOrderGoodsService.list(queryWrapper).stream()
            .map(row -> BeanUtil.copyProperties(row, TdOrderGoodsDTO.class))
            .toList();
    }
}
