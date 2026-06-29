package pub.module.trade.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.trade.crud.entity.TdGoods;
import pub.module.trade.crud.entity.TdGoodsCategory;
import pub.module.trade.crud.mapper.TdGoodsMapper;
import pub.module.trade.crud.service.ITdGoodsBenefitService;
import pub.module.trade.crud.service.ITdGoodsCategoryService;
import pub.module.trade.crud.service.ITdGoodsService;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 商品服务实现类
 * 提供商品相关的业务逻辑实现
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Service
public class TdGoodsServiceImpl extends ServiceImpl<TdGoodsMapper, TdGoods> implements ITdGoodsService {

    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.9000");

    @Resource
    private ITdGoodsCategoryService tdGoodsCategoryService;

    @Resource
    private ITdGoodsBenefitService tdGoodsBenefitService;

    private void saveBenefitList(TdGoods entity) {
        if (entity.getBenefitList() == null) {
            return;
        }
        Assert.notBlank(entity.getTdGdCode(), "tdGdCode 不能为空");
        tdGoodsBenefitService.replaceByTdGdCode(entity.getTdGdCode(), entity.getBenefitList());
    }

    private void loadBenefitList(TdGoods entity) {
        if (entity == null || StrUtil.isBlank(entity.getTdGdCode())) {
            return;
        }
        entity.setBenefitList(tdGoodsBenefitService.listByTdGdCode(entity.getTdGdCode()));
    }


    public void setDefaultValue(TdGoods entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "tdGdCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
        if (entity.getTdGdCommissionRate() == null) {
            entity.setTdGdCommissionRate(DEFAULT_COMMISSION_RATE);
        }
        if (StrUtil.isBlank(entity.getTdGdEnabledCode())) {
            entity.setTdGdEnabledCode("1");
        }
    }

    private void syncCategoryName(TdGoods entity) {
        if (StrUtil.isBlank(entity.getTdGdCgyCode())) {
            entity.setTdGdCgyName(null);
            return;
        }
        TdGoodsCategory category = tdGoodsCategoryService.lambdaQuery()
                .eq(TdGoodsCategory::getTdGdCgyCode, entity.getTdGdCgyCode())
                .one();
        entity.setTdGdCgyName(category == null ? null : category.getTdGdCgyName());
    }


    @Override
    @Transactional
    public boolean save(TdGoods entity) {
        this.setDefaultValue(entity);
        this.syncCategoryName(entity);
        this.getBaseMapper().insert(entity);
        this.saveBenefitList(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<TdGoods> entityList) {
        for ( TdGoods entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         TdGoods entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "TdGoods不存在");
         tdGoodsBenefitService.removeByTdGdCode(entity.getTdGdCode());
         this.getBaseMapper().deleteById(id);

         return true;
     }

    @Transactional
       @Override
       public boolean removeByIds(Collection<?> ids) {
           ids.forEach(entity -> this.removeById((Serializable) entity));
           return true;
       }

    @Override
    @Transactional
    public boolean updateById(TdGoods entity) {
        this.syncCategoryName(entity);
        this.getBaseMapper().updateById(entity);
        this.saveBenefitList(entity);

        return true;
    }

    @Override
    public TdGoods getById(Serializable id) {
        TdGoods entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "TdGoods不存在");
        this.setDefaultValue(entity);
        this.loadBenefitList(entity);
        return entity;
    }

    @Override
    public TdGoods getOne(Wrapper<TdGoods> queryWrapper,
                          boolean throwEx) {
        TdGoods entity = null;
        List<TdGoods> list = this.list(queryWrapper);
        if (!list.isEmpty()) {
            entity = list.get(0);
            this.setDefaultValue(entity);
        }
        if (throwEx && list.size() > 1) {
            throw new IllegalArgumentException("查询条件有误，查询到多条数据");
        }
        return entity;
    }
}
