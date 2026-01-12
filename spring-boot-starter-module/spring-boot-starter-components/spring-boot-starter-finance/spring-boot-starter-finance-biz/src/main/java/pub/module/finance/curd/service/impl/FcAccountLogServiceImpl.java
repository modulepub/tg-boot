package pub.module.finance.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.finance.curd.entity.FcAccountLog;
import pub.module.finance.curd.mapper.FcAccountLogMapper;
import pub.module.finance.curd.service.IFcAccountLogService;
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
 * 金融账户变动日志
 * @author tg
 * @since 2025-09-30
 * @version V1.0
 */
@Slf4j
@Service
public class FcAccountLogServiceImpl extends ServiceImpl<FcAccountLogMapper, FcAccountLog> implements IFcAccountLogService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(FcAccountLog entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "fcAcLogCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(FcAccountLog entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<FcAccountLog> entityList) {
        for ( FcAccountLog entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         FcAccountLog entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "FcAccountLog不存在");
         this.getBaseMapper().deleteById(id);
         
         return true;
     }

    @Override
    @Transactional
    public boolean updateById(FcAccountLog entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public FcAccountLog getById(Serializable id) {
        FcAccountLog entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcAccountLog不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public FcAccountLog getOne(Wrapper<FcAccountLog> queryWrapper,
                          boolean throwEx) {
        FcAccountLog entity = null;
        List<FcAccountLog> list = this.list(queryWrapper);
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
