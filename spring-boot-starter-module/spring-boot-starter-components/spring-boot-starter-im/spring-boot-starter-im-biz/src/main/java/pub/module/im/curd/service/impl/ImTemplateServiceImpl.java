package pub.module.im.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.im.curd.entity.ImTemplate;
import pub.module.im.curd.mapper.ImTemplateMapper;
import pub.module.im.curd.service.IImTemplateService;
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
 * 消息模板
 * @author tg
 * @since 2025-10-27
 * @version V1.0
 */
@Slf4j
@Service
public class ImTemplateServiceImpl extends ServiceImpl<ImTemplateMapper, ImTemplate> implements IImTemplateService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(ImTemplate entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "imTemplateCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(ImTemplate entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<ImTemplate> entityList) {
        for ( ImTemplate entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         ImTemplate entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "ImTemplate不存在");
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
    public boolean updateById(ImTemplate entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public ImTemplate getById(Serializable id) {
        ImTemplate entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "ImTemplate不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public ImTemplate getOne(Wrapper<ImTemplate> queryWrapper,
                          boolean throwEx) {
        ImTemplate entity = null;
        List<ImTemplate> list = this.list(queryWrapper);
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
