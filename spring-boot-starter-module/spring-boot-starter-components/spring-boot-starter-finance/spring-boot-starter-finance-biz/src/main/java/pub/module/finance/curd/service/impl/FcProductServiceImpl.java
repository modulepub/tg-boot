package pub.module.finance.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.finance.curd.entity.FcProduct;
import pub.module.finance.curd.mapper.FcProductMapper;
import pub.module.finance.curd.service.IFcProductService;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Field;

import cn.hutool.core.util.ReflectUtil;


/**
 * 产品管理
 * @author tg
 * @since 2025-10-11
 * @version V1.0
 */
@Slf4j
@Service
public class FcProductServiceImpl extends ServiceImpl<FcProductMapper, FcProduct> implements IFcProductService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(FcProduct entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "fcProductCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(FcProduct entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<FcProduct> entityList) {
        for ( FcProduct entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         FcProduct entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "FcProduct不存在");
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
    public boolean updateById(FcProduct entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public FcProduct getById(Serializable id) {
        FcProduct entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcProduct不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public FcProduct getOne(Wrapper<FcProduct> queryWrapper,
                          boolean throwEx) {
        FcProduct entity = null;
        List<FcProduct> list = this.list(queryWrapper);
        if (!list.isEmpty()) {
            entity = list.get(0);
            this.setDefaultValue(entity);
        }
        if (throwEx && list.size() > 1) {
            throw new IllegalArgumentException("查询条件有误，查询到多条数据");
        }
        return entity;
    }

    @Override
    public FcProduct getByCode(String code) {
        return this.getOne(new QueryWrapper<FcProduct>().lambda()
                        .eq(FcProduct::getFcProductCode,code)
                ,false);
    }
}
