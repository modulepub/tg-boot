package pub.module.trade.crud.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.trade.crud.entity.TdOrderGoods;
import pub.module.trade.crud.mapper.TdOrderGoodsMapper;
import pub.module.trade.crud.service.ITdOrderGoodsService;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 订单商品服务实现类
 * 提供订单商品相关的业务逻辑实现
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Service
public class TdOrderGoodsServiceImpl extends ServiceImpl<TdOrderGoodsMapper, TdOrderGoods> implements ITdOrderGoodsService {



    public void setDefaultValue(TdOrderGoods entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "tdGdCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
        Field odGdCodeField = ReflectUtil.getField(entity.getClass(), "tdOdGdCode");
        Assert.notNull(odGdCodeField, "tdOdGdCode 字段未设置");
        Object odGdCodeVal = ReflectUtil.getFieldValue(entity, odGdCodeField);
        if (odGdCodeVal == null || StrUtil.isBlank(odGdCodeVal.toString())) {
            ReflectUtil.setFieldValue(entity, odGdCodeField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(TdOrderGoods entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<TdOrderGoods> entityList) {
        for ( TdOrderGoods entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         TdOrderGoods entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "TdOrderGoods不存在");
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
    public boolean updateById(TdOrderGoods entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public TdOrderGoods getById(Serializable id) {
        TdOrderGoods entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "TdOrderGoods不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public TdOrderGoods getOne(Wrapper<TdOrderGoods> queryWrapper,
                          boolean throwEx) {
        TdOrderGoods entity = null;
        List<TdOrderGoods> list = this.list(queryWrapper);
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
