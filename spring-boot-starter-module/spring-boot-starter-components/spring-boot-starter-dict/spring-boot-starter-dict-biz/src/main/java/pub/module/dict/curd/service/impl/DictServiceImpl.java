package pub.module.dict.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.dict.curd.entity.Dict;
import pub.module.dict.curd.mapper.DictMapper;
import pub.module.dict.curd.service.IDictService;
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
 * sys_dict
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

    public void setDefaultValue(Dict entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "dictCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(Dict entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<Dict> entityList) {
        for ( Dict entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         Dict entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "SysDict不存在");
         this.getBaseMapper().deleteById(id);
         
         return true;
     }

    @Override
    @Transactional
    public boolean updateById(Dict entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public Dict getById(Serializable id) {
        Dict entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysDict不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public Dict getOne(Wrapper<Dict> queryWrapper,
                       boolean throwEx) {
        Dict entity = null;
        List<Dict> list = this.list(queryWrapper);
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
