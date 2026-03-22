package pub.module.trade.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.trade.curd.entity.TdOrder;
import pub.module.trade.curd.mapper.TdOrderMapper;
import pub.module.trade.curd.service.ITdOrderService;
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
 * 订单服务实现类
 * 提供订单相关的业务逻辑实现
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@Service
public class TdOrderServiceImpl extends ServiceImpl<TdOrderMapper, TdOrder> implements ITdOrderService {



    public void setDefaultValue(TdOrder entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "tdOdCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(TdOrder entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<TdOrder> entityList) {
        for ( TdOrder entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         TdOrder entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "TdOrder不存在");
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
    public boolean updateById(TdOrder entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public TdOrder getById(Serializable id) {
        TdOrder entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "TdOrder不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public TdOrder getOne(Wrapper<TdOrder> queryWrapper,
                          boolean throwEx) {
        TdOrder entity = null;
        List<TdOrder> list = this.list(queryWrapper);
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
