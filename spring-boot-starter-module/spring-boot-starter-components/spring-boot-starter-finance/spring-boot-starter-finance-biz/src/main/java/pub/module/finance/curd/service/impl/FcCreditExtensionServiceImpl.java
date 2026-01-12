package pub.module.finance.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;

import pub.module.finance.curd.entity.FcCreditExtension;
import pub.module.finance.curd.mapper.FcCreditExtensionMapper;
import pub.module.finance.curd.service.IFcCreditExtensionService;
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
 * 授信表
 * @author tg
 * @since 2025-11-02
 * @version V1.0
 */
@Slf4j
@Service
public class FcCreditExtensionServiceImpl extends ServiceImpl<FcCreditExtensionMapper, FcCreditExtension> implements IFcCreditExtensionService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(FcCreditExtension entity) {
        Field declaredField = ReflectUtil.getField(entity.getClass(), "fcCdExCode");
           Assert.notNull(declaredField,"CODE 字段名称未設置");
        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
        }
    }


    @Override
    @Transactional
    public boolean save(FcCreditExtension entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);
        
        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<FcCreditExtension> entityList) {
        for ( FcCreditExtension entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         FcCreditExtension entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "FcCreditExtension不存在");
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
    public boolean updateById(FcCreditExtension entity) {
        this.getBaseMapper().updateById(entity);
        
        return true;
    }

    @Override
    public FcCreditExtension getById(Serializable id) {
        FcCreditExtension entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "FcCreditExtension不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public FcCreditExtension getOne(Wrapper<FcCreditExtension> queryWrapper,
                          boolean throwEx) {
        FcCreditExtension entity = null;
        List<FcCreditExtension> list = this.list(queryWrapper);
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
