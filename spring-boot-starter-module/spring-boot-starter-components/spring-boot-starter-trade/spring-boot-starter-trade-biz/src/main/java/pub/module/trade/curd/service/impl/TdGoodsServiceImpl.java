package pub.module.trade.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.trade.curd.entity.TdGoods;
import pub.module.trade.curd.mapper.TdGoodsMapper;
import pub.module.trade.curd.service.ITdGoodsService;
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
 * 商品服务实现类
 * 提供商品相关的业务逻辑实现
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Service
public class TdGoodsServiceImpl extends ServiceImpl<TdGoodsMapper, TdGoods> implements ITdGoodsService {



    public void setDefaultValue(TdGoods entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "tdGdCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(TdGoods entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

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
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public TdGoods getById(Serializable id) {
        TdGoods entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "TdGoods不存在");
        this.setDefaultValue(entity);
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
