package pub.module.trade.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.module.trade.api.dto.TdGoodsDTO;
import pub.module.trade.api.service.ApiTdGoodsService;
import pub.module.trade.curd.entity.TdGoods;
import pub.module.trade.curd.service.ITdGoodsService;

import java.util.Collections;
import java.util.List;

/**
 * 商品业务实现
 *
 * @author tg
 * @since 2026-05-04
 */
@Service
public class ApiTdGoodsServiceImpl implements ApiTdGoodsService {

    @Resource
    private ITdGoodsService tdGoodsService;

    @Override
    public String addGoods(TdGoodsDTO dto) {
        Assert.notNull(dto, "TdGoodsDTO 不能为空");
        TdGoods entity = BeanUtil.copyProperties(dto, TdGoods.class);
        tdGoodsService.save(entity);
        return entity.getTdGdCode();
    }

    @Transactional
    @Override
    public List<TdGoodsDTO> listByTdGdSysUserCode(String tdGdSysUserCode) {
        Assert.isTrue(StrUtil.isNotBlank(tdGdSysUserCode), "tdGdSysUserCode 不能为空");
        List<TdGoods> list = tdGoodsService.lambdaQuery()
                .eq(TdGoods::getTdGdSysUserCode, tdGdSysUserCode)
                .orderByAsc(TdGoods::getSeqNo)
                .list();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(list, TdGoodsDTO.class);
    }
}
