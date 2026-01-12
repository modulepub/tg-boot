package pub.module.im.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.im.curd.entity.ImCsr;
import pub.module.im.curd.mapper.ImCsrMapper;
import pub.module.im.curd.service.IImCsrService;
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
 * 客服坐席
 * @author tg
 * @since 2025-10-03
 * @version V1.0
 */
@Slf4j
@Service
public class ImCsrServiceImpl extends ServiceImpl<ImCsrMapper, ImCsr> implements IImCsrService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(ImCsr entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "imCsrCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(ImCsr entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<ImCsr> entityList) {
        for ( ImCsr entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         ImCsr entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "ImCsr不存在");
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
    public boolean updateById(ImCsr entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public ImCsr getById(Serializable id) {
        ImCsr entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "ImCsr不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public ImCsr getOne(Wrapper<ImCsr> queryWrapper,
                          boolean throwEx) {
        ImCsr entity = null;
        List<ImCsr> list = this.list(queryWrapper);
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
