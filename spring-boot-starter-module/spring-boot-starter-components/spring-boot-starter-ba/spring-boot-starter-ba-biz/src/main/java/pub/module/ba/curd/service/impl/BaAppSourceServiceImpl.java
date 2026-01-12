package pub.module.ba.curd.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import lombok.extern.slf4j.Slf4j;
import pub.module.ba.curd.entity.BaAppSource;
import pub.module.ba.curd.mapper.BaAppSourceMapper;
import pub.module.ba.curd.service.IBaAppSourceService;

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
 * 用户行为_渠道
 * @author tg
 * @since 2025-10-11
 * @version V1.0
 */
@Slf4j
@Service
public class BaAppSourceServiceImpl extends ServiceImpl<BaAppSourceMapper, BaAppSource> implements IBaAppSourceService {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    public void setDefaultValue(BaAppSource entity) {
//        Field declaredField = ReflectUtil.getField(entity.getClass(), "taCode");
//           Assert.notNull(declaredField,"CODE 字段名称未設置");
//        if (ReflectUtil.getFieldValue(entity, declaredField) == null) {
//            ReflectUtil.setFieldValue(entity, declaredField, IdUtil.getSnowflakeNextIdStr());
//        }
    }


    @Override
    @Transactional
    public boolean save(BaAppSource entity) {
        this.setDefaultValue(entity);
        this.getBaseMapper().insert(entity);

        return true;
    }

    @Override
    @Transactional
    public boolean saveBatch(Collection<BaAppSource> entityList) {
        for ( BaAppSource entity : entityList) {
            this.save(entity);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeById(Serializable id) {
         BaAppSource entity = this.getBaseMapper().selectById(id);
         Assert.notNull(entity, "BaAppSource不存在");
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
    public boolean updateById(BaAppSource entity) {
        this.getBaseMapper().updateById(entity);

        return true;
    }

    @Override
    public BaAppSource getById(Serializable id) {
        BaAppSource entity = this.getBaseMapper().selectById(id);
        Assert.notNull(entity, "BaAppSource不存在");
        this.setDefaultValue(entity);
        return entity;
    }

    @Override
    public BaAppSource getOne(Wrapper<BaAppSource> queryWrapper,
                          boolean throwEx) {
        BaAppSource entity = null;
        List<BaAppSource> list = this.list(queryWrapper);
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
