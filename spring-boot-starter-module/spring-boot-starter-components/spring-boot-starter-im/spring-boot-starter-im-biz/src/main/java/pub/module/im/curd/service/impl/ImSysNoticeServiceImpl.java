package pub.module.im.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.im.curd.entity.ImSysNotice;
import pub.module.im.curd.mapper.ImSysNoticeMapper;
import pub.module.im.curd.service.IImSysNoticeService;
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
 * 系统通知
 * @author tg
 * @since 2025-10-05
 * @version V1.0
 */
@Slf4j
@Service
public class ImSysNoticeServiceImpl extends ServiceImpl<ImSysNoticeMapper, ImSysNotice> implements IImSysNoticeService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(ImSysNotice entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "imSysNoticeCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(ImSysNotice entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<ImSysNotice> entityList) {
        for ( ImSysNotice entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         ImSysNotice entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "ImSysNotice不存在");
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
    public boolean updateById(ImSysNotice entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public ImSysNotice getById(Serializable id) {
        ImSysNotice entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "ImSysNotice不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public ImSysNotice getOne(Wrapper<ImSysNotice> queryWrapper,
                          boolean throwEx) {
        ImSysNotice entity = null;
        List<ImSysNotice> list = this.list(queryWrapper);
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
