package pub.module.contract.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.contract.curd.entity.CtTemplate;
import pub.module.contract.curd.mapper.CtTemplateMapper;
import pub.module.contract.curd.service.ICtTemplateService;
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
 * ct_template
 * @author tg
 * @since 2025-12-09
 * @version V1.0
 */
@Slf4j
@Service
public class CtTemplateServiceImpl extends ServiceImpl<CtTemplateMapper, CtTemplate> implements ICtTemplateService {



    public void setDefaultValue(CtTemplate entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "ctTemplateCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(CtTemplate entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<CtTemplate> entityList) {
        for ( CtTemplate entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         CtTemplate entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "CtTemplate不存在");
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
    public boolean updateById(CtTemplate entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public CtTemplate getById(Serializable id) {
        CtTemplate entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "CtTemplate不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public CtTemplate getOne(Wrapper<CtTemplate> queryWrapper,
                          boolean throwEx) {
        CtTemplate entity = null;
        List<CtTemplate> list = this.list(queryWrapper);
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
