package pub.module.dict.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import pub.module.dict.api.constants.DictCacheKey;
import pub.module.dict.curd.entity.DictItem;
import pub.module.dict.curd.mapper.DictItemMapper;
import pub.module.dict.curd.service.DictItemService;
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
 * sys_dict_item
 * @author tg
 * @since 2025-09-29
 * @version V1.0
 */
@Slf4j
@Service
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {


    public void setDefaultValue(DictItem entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "dictItemCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }

    @CacheEvict(value = DictCacheKey.DICT_CACHE_KEY, key = "#entity.dictCode")
    @Override
    @Transactional
    public boolean save(DictItem entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<DictItem> entityList) {
        for ( DictItem entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         DictItem entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "SysDictItem 不存在");
         this.getBaseMapper().deleteById(id);
         
         return true;
     }

    @Override
    @Transactional
    @CacheEvict(value = DictCacheKey.DICT_CACHE_KEY, key = "#entity.dictCode")
    public boolean updateById(DictItem entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public DictItem getById(Serializable id) {
        DictItem entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "SysDictItem不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public DictItem getOne(Wrapper<DictItem> queryWrapper,
                           boolean throwEx) {
        DictItem entity = null;
        List<DictItem> list = this.list(queryWrapper);
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
